package io.jenkins.plugins.github.release;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.github.GitHubUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class UploadReleaseAssetStepExecution extends SynchronousStepExecution<Void> {
  private final UploadReleaseAssetStep step;

  protected UploadReleaseAssetStepExecution(UploadReleaseAssetStep step, StepContext context) {
    super(context);
    this.step = step;
  }

  @Override
  protected Void run() throws Exception {
    // Get the workspace FilePath for the node
    FilePath workspace = getContext().get(FilePath.class);
    TaskListener taskListener = this.getContext().get(TaskListener.class);
    GitHub gitHub = GitHubUtils.loginToGithub(this.step, taskListener);
    GHRepository repository = GitHubUtils.getRepository(gitHub, this.step);
    GHRelease release = repository.getReleaseByTagName(this.step.tagName);

    if (this.step.uploadAssets == null || this.step.uploadAssets.isEmpty()) {
      throw new IllegalStateException(
          "uploadAssets cannot be null or empty."
      );
    }

    List<UploadAsset> missingUploads = this.step.uploadAssets
        .stream()
        .filter(uploadAsset -> uploadAsset.isMissing(workspace))
        .collect(Collectors.toList());

    if (!missingUploads.isEmpty()) {
      List<String> missingFilePaths = missingUploads
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

    for (UploadAsset uploadAsset : this.step.uploadAssets) {
      taskListener.getLogger().printf("Started uploading %s%n", uploadAsset.filePath);
      try (InputStream assetStream = uploadAsset.toStream(workspace)) {
        release.uploadAsset(
            uploadAsset.filePath,
            assetStream,
            uploadAsset.contentType
        );
      }
      taskListener.getLogger().printf("Finished uploading %s%n", uploadAsset.filePath);
    }

    return null;
  }
}
