package io.jenkins.plugins.github;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.model.TaskListener;
import hudson.security.ACL;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitHubUtils {
    public static GitHub loginToGithub(GitHubParameters parameters, TaskListener listener)
            throws IOException, InterruptedException {
        if (null == parameters.getCredentialId()) {
            throw new IllegalArgumentException("credentialId cannot be null.");
        }

        List<StringCredentials> credentialList = CredentialsProvider.lookupCredentials(
                StringCredentials.class, Jenkins.get(), ACL.SYSTEM, Collections.emptyList());
        Optional<StringCredentials> credentials = credentialList.stream()
                .filter(p -> parameters.getCredentialId().equals(p.getId()))
                .findFirst();

        if (credentials.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("credentialId '%s' was not found", parameters.getCredentialId()));
        }

        GitHub gitHub;

        if (null != parameters.getGithubServer()) {
            listener.getLogger().printf("Connecting to %s", parameters.getGithubServer());
            listener.getLogger().println();
            gitHub = GitHub.connectUsingOAuth(
                    parameters.getGithubServer(), credentials.get().getSecret().getPlainText());
        } else {
            gitHub = GitHub.connectUsingOAuth(credentials.get().getSecret().getPlainText());
        }

        return gitHub;
    }

    public static GHRepository getRepository(GitHub gitHub, RepositoryParameters parameters) throws IOException {
        if (null == parameters.getRepository()) {
            throw new IllegalArgumentException("repository must be set.");
        }
        return gitHub.getRepository(parameters.getRepository());
    }
}
