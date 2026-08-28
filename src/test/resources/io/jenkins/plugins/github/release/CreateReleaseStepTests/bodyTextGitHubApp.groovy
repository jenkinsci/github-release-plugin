package io.jenkins.plugins.github.release.CreateReleaseStepTests

node {
    createGitHubRelease(
            credentialId: 'app-credential',
            githubServer: '%s',
            repository: 'jcustenborder/xjc-kafka-connect-plugin',
            tag: 'v1.2.3.4',
            commitish: '17b5676aaab28e334c0a9befc86e7615a7539c32',
            bodyText: 'This is a test message.',
            draft: true
    )
}