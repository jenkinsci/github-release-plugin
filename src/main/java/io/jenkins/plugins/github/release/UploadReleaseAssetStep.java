package io.jenkins.plugins.github.release;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.github.GitHubParameters;
import io.jenkins.plugins.github.RepositoryParameters;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class UploadReleaseAssetStep extends Step implements Serializable, GitHubParameters, RepositoryParameters {

  public String tagName;
  public List<UploadAsset> uploadAssets;
  public String credentialId;
  public String githubServer;
  public String repository;
  public Boolean overwrite = false;

  @DataBoundConstructor
  public UploadReleaseAssetStep(String tagName) {
    setTagName(tagName);
  }

  @DataBoundSetter
  public void setTagName(String tagName) {
    this.tagName = Util.fixEmptyAndTrim(tagName);
  }

  @DataBoundSetter
  public void setUploadAssets(List<UploadAsset> uploadAssets) {
    this.uploadAssets = uploadAssets;
  }
  @Override
  public String getCredentialId() {
    return this.credentialId;
  }

  @DataBoundSetter
  @Override
  public void setCredentialId(String credentialId) {
    this.credentialId = Util.fixEmptyAndTrim(credentialId);
  }

  @Override
  public String getGithubServer() {
    return this.githubServer;
  }

  @DataBoundSetter
  @Override
  public void setGithubServer(String gitHubServer) {
    this.githubServer = Util.fixEmptyAndTrim(gitHubServer);
  }


  @Override
  public String getRepository() {
    return this.repository;
  }

  @DataBoundSetter
  @Override
  public void setRepository(String repository) {
    this.repository = Util.fixEmptyAndTrim(repository);
  }

  @DataBoundSetter
  public void setOverwrite(Boolean overwrite) {
    this.overwrite = overwrite;
  }

  public StepExecution start(StepContext context) throws Exception {
    return new UploadReleaseAssetStepExecution(this, context);
  }

  @Extension
  public static class DescriptorImpl extends AbstractReleaseDescriptor {
    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(Run.class, TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "uploadGithubReleaseAsset";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "uploadGithubReleaseAsset";
    }
  }
}
