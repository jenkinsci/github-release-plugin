package org.jenkinsci.plugins.github;

public interface GitHubParameters {
  String getCredentialId();
  void setCredentialId(String credentialId);


  String getGithubServer();
  void setGithubServer(String gitHubServer);
}
