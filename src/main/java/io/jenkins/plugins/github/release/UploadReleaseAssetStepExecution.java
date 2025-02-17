package io.jenkins.plugins.github.release;

import hudson.model.TaskListener;
import io.jenkins.plugins.github.GitHubUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class UploadReleaseAssetStepExecution extends SynchronousStepExecution<Void> {
    private final UploadReleaseAssetStep step;

    protected UploadReleaseAssetStepExecution(UploadReleaseAssetStep step, StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    protected Void run() throws Exception {
        TaskListener taskListener = this.getContext().get(TaskListener.class);
        GitHub gitHub = GitHubUtils.loginToGithub(this.step, taskListener);
        GHRepository repository = GitHubUtils.getRepository(gitHub, this.step);
        GHRelease release = repository.getReleaseByTagName(this.step.tagName);

        if (this.step.uploadAssets == null || this.step.uploadAssets.isEmpty()) {
            throw new IllegalStateException("uploadAssets cannot be null or empty.");
        }

        List<UploadAsset> missingUploads =
                this.step.uploadAssets.stream().filter(UploadAsset::isMissing).collect(Collectors.toList());

        if (!missingUploads.isEmpty()) {
            List<String> missingFilePaths =
                    missingUploads.stream().map(e -> e.filePath).collect(Collectors.toList());

            throw new IllegalStateException(String.format(
                    "%s file(s) to upload were missing: %s",
                    missingFilePaths.size(), String.join(", ", missingFilePaths)));
        }

        for (UploadAsset uploadAsset : this.step.uploadAssets) {
            taskListener.getLogger().printf("Started uploading %s%n", uploadAsset.filePath);
            release.uploadAsset(uploadAsset.toFile(), uploadAsset.contentType);
            taskListener.getLogger().printf("Finished uploading %s%n", uploadAsset.filePath);
        }

        return null;
    }
}
