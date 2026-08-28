package io.jenkins.plugins.github;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.model.TaskListener;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GitHubUtils {
  public static GitHub loginToGithub(GitHubParameters parameters, TaskListener listener) throws IOException, InterruptedException {
    if (null == parameters.getCredentialId()) {
      throw new IllegalArgumentException("credentialId cannot be null.");
    }

    List<StringCredentials> credentialList = CredentialsProvider.lookupCredentials(StringCredentials.class, Jenkins.get(), ACL.SYSTEM, Collections.emptyList());
    Optional<StringCredentials> credentials = credentialList.stream().filter(p -> parameters.getCredentialId().equals(p.getId())).findFirst();

    if (credentials.isPresent()) {
      return connect(parameters, listener, credentials.get().getSecret().getPlainText());
    }

    List<StandardUsernamePasswordCredentials> appCredentialList = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, Jenkins.get(), ACL.SYSTEM, Collections.emptyList());
    Optional<StandardUsernamePasswordCredentials> appCredentials = appCredentialList.stream().filter(p -> parameters.getCredentialId().equals(p.getId())).findFirst();

    if (appCredentials.isPresent()) {
      return connect(parameters, listener, appCredentials.get().getPassword().getPlainText());
    }

    throw new IllegalArgumentException(
        String.format("credentialId '%s' was not found", parameters.getCredentialId())
    );
  }

  private static GitHub connect(GitHubParameters parameters, TaskListener listener, String token) throws IOException {
    if (null != parameters.getGithubServer()) {
      listener.getLogger().printf("Connecting to %s", parameters.getGithubServer());
      listener.getLogger().println();
      return GitHub.connectUsingOAuth(parameters.getGithubServer(), token);
    } else {
      return GitHub.connectUsingOAuth(token);
    }
  }

  public static GHRepository getRepository(GitHub gitHub, RepositoryParameters parameters) throws IOException {
    if (null == parameters.getRepository()) {
      throw new IllegalArgumentException(
          "repository must be set."
      );
    }
    return gitHub.getRepository(parameters.getRepository());
  }
}
