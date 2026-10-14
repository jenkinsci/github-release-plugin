package io.jenkins.plugins.github.release;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.github.GitHubParameters;
import jenkins.model.Jenkins;
import io.jenkins.plugins.github.RepositoryParameters;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.Serializable;
import java.util.Set;

public class ListReleasesStep extends Step implements Serializable, GitHubParameters, RepositoryParameters {

  public String tagNamePattern;
  public Boolean includeDrafts;
  public String sortBy;
  public Boolean sortAscending = true;
  String credentialId;
  String githubServer;
  String repository;

  @DataBoundConstructor
  public ListReleasesStep() {

  }

  @DataBoundSetter
  public void setTagNamePattern(String tagNamePattern) {
    this.tagNamePattern = Util.fixEmptyAndTrim(tagNamePattern);
  }

  @DataBoundSetter
  public void setIncludeDrafts(Boolean includeDrafts) {
    this.includeDrafts = includeDrafts;
  }

  public StepExecution start(StepContext context) throws Exception {
    return new ListReleaseStepExecution(this, context);
  }

  @DataBoundSetter
  public void setSortBy(String sortBy) {
    this.sortBy = Util.fixEmptyAndTrim(sortBy);
  }

  @DataBoundSetter
  public void setSortAscending(Boolean sortAscending) {
    this.sortAscending = sortAscending;
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


  @Extension
  public static class DescriptorImpl extends AbstractReleaseDescriptor {
    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(Run.class, TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "listGitHubReleases";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "listGitHubReleases";
    }

    @RequirePOST
    public ListBoxModel doFillSortByItems(@AncestorInPath Item context) {
      if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER) ||
          context != null && !context.hasPermission(Item.EXTENDED_READ)) {
        return new StandardListBoxModel();
      }

      return new StandardListBoxModel()
          .includeEmptyValue()
          .add("SymantecVersion");
    }
  }

}
