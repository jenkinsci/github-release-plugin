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
import java.io.Serializable;
import java.util.Set;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class CreateReleaseStep extends Step implements Serializable, GitHubParameters, RepositoryParameters {

    public String tag;
    public String bodyText;
    public String bodyFile;
    public String commitish;
    public Boolean draft;
    public String name;
    public Boolean prerelease;
    public String categoryName;
    public String credentialId;
    public String gitHubServer;
    public String repository;

    @DataBoundConstructor
    public CreateReleaseStep() {}

    public StepExecution start(StepContext context) throws Exception {
        return new CreateReleaseStepExecution(this, context);
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
        return this.gitHubServer;
    }

    @DataBoundSetter
    @Override
    public void setGithubServer(String gitHubServer) {
        this.gitHubServer = Util.fixEmptyAndTrim(gitHubServer);
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
    public void setTag(String tag) {
        this.tag = Util.fixEmptyAndTrim(tag);
    }

    @DataBoundSetter
    public void setBodyText(String bodyText) {
        this.bodyText = Util.fixEmptyAndTrim(bodyText);
    }

    @DataBoundSetter
    public void setBodyFile(String bodyFile) {
        this.bodyFile = Util.fixEmptyAndTrim(bodyFile);
    }

    @DataBoundSetter
    public void setCommitish(String commitish) {
        this.commitish = Util.fixEmptyAndTrim(commitish);
    }

    @DataBoundSetter
    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    @DataBoundSetter
    public void setName(String name) {
        this.name = Util.fixEmptyAndTrim(name);
    }

    @DataBoundSetter
    public void setPrerelease(Boolean prerelease) {
        this.prerelease = prerelease;
    }

    @DataBoundSetter
    public void setCategoryName(String categoryName) {
        this.categoryName = Util.fixEmptyAndTrim(categoryName);
    }

    @Extension
    public static class DescriptorImpl extends AbstractReleaseDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class, FilePath.class);
        }

        @Override
        public String getFunctionName() {
            return "createGitHubRelease";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "createGitHubRelease";
        }
    }
}
