package io.jenkins.plugins.github.release.ListReleaseStepTests

node {
    def foo = listGitHubReleases(
            credentialId: 'app-credential',
            githubServer: '%s',
            repository: 'jcustenborder/xjc-kafka-connect-plugin'
    )
    if (!foo) {
        throw new Exception("Releases should not be null.")
    }
    if(foo.size() != 13) {
        throw new Exception("13 Releases should be present. ${foo.size()} was found.")
    }
}