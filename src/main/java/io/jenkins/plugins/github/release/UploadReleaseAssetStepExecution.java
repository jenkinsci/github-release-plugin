package io.jenkins.plugins.github.release;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.github.GitHubUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class UploadReleaseAssetStepExecution extends SynchronousStepExecution<Void> {
  private final UploadReleaseAssetStep step;

  protected UploadReleaseAssetStepExecution(final UploadReleaseAssetStep step, final StepContext context) {
    super(context);
    this.step = step;
  }

  @Override
  protected Void run() throws Exception {
    // Get the workspace FilePath for the node
    final FilePath workspace = getContext().get(FilePath.class);
    final TaskListener taskListener = this.getContext().get(TaskListener.class);
    final GitHub gitHub = GitHubUtils.loginToGithub(this.step, taskListener);
    final GHRepository repository = GitHubUtils.getRepository(gitHub, this.step);
    GHRelease release = repository.getReleaseByTagName(this.step.tagName);

    if (release == null) {
      taskListener.getLogger().printf(
          "No published release found for tag '%s', searching all releases including drafts...%n", this.step.tagName);
      int maxAttempts = 3;
      for (int attempt = 0; attempt < maxAttempts && release == null; attempt++) {
        if (attempt > 0) {
          taskListener.getLogger().printf(
              "Draft release not yet visible, retrying in 3s (attempt %d/%d)...%n", attempt + 1, maxAttempts);
          Thread.sleep(3000);
        }
        for (GHRelease candidate : repository.listReleases()) {
          if (this.step.tagName.equals(candidate.getTagName())) {
            release = candidate;
            break;
          }
        }
      }
    }

    if (release == null) {
      throw new IllegalStateException(
          String.format("No release found with tag '%s'", this.step.tagName));
    }

    if (this.step.uploadAssets == null || this.step.uploadAssets.isEmpty()) {
      throw new IllegalStateException(
          "uploadAssets cannot be null or empty."
      );
    }

    final List<UploadAsset> missingUploads = this.step.uploadAssets
        .stream()
        .filter(uploadAsset -> uploadAsset.isMissing(workspace))
        .collect(Collectors.toList());

    if (!missingUploads.isEmpty()) {
      final List<String> missingFilePaths = missingUploads
          .stream()
          .map(e -> e.filePath)
          .collect(Collectors.toList());

      throw new IllegalStateException(
          String.format(
              "%s file(s) to upload were missing: %s",
              missingFilePaths.size(),
              String.join(", ", missingFilePaths)
          )
      );
    }

    List<GHAsset> existingAssets = null;
    if (this.step.overwrite) {
      existingAssets = release.listAssets().toList();
    }

    for (final UploadAsset uploadAsset : this.step.uploadAssets) {
      final String assetName = new File(uploadAsset.filePath).getName();

      if (this.step.overwrite && existingAssets != null) {
        final List<GHAsset> duplicates = existingAssets.stream()
            .filter(asset -> asset.getName().equals(assetName))
            .collect(Collectors.toList());

        for (final GHAsset duplicate : duplicates) {
          taskListener.getLogger().printf(
              "Found existing asset matching '%s' (ID: %d). Overwrite is enabled, deleting...%n",
              assetName,
              duplicate.getId()
          );
          duplicate.delete();
        }
      }

      taskListener.getLogger().printf("Started uploading %s%n", uploadAsset.filePath);
      try (final InputStream assetStream = uploadAsset.toStream(workspace)) {
        release.uploadAsset(
            new File(uploadAsset.filePath).getName(),
            assetStream,
            uploadAsset.contentType
        );
      }
      taskListener.getLogger().printf("Finished uploading %s%n", uploadAsset.filePath);
    }

    return null;
  }
}
