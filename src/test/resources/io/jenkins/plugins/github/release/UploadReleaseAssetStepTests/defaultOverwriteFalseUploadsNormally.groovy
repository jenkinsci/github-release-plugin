package io.jenkins.plugins.github.release.UploadReleaseAssetStepTests

node {
    writeFile file: 'app.jar', text: 'dummy payload'
    uploadGithubReleaseAsset(
            tagName: 'v1.2.3',
            credentialId: 'a1234',
            githubServer: '%s',
            repository: 'jcustenborder/xjc-kafka-connect-plugin',
            uploadAssets: [[filePath: 'app.jar', contentType: 'application/java-archive']]
    )
}