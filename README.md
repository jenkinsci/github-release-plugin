# Introduction

The GitHub Release plugin provides pipeline steps to create and query GitHub releases.

## Authentication

### Personal Access Token

Create a Jenkins credential of kind **Secret text** containing your GitHub personal access token, then pass its ID as `credentialId`.

### GitHub App (GitHub Enterprise)

If personal access tokens are disabled on your GitHub Enterprise instance, authenticate as a GitHub App instead:

1. Create a GitHub App on your GHE instance and grant it **Contents: Read & write** and **Metadata: Read-only** permissions.
2. Install the [GitHub Branch Source plugin](https://plugins.jenkins.io/github-branch-source/).
3. GitHub generates private keys in PKCS#1 format. The Jenkins credential requires PKCS#8. Convert before uploading:
   ```bash
   openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in downloaded-key.pem -out key-pkcs8.pem
   ```
4. In Jenkins, add a credential of kind **GitHub App** (App ID + converted private key PEM).
5. Pass that credential's ID as `credentialId`. The plugin fetches and refreshes the installation access token automatically.

## Examples

### createGitHubRelease

Step is used to create a new release in a GitHub repository.

#### Create a release using a markdown file for the body
```groovy
writeFile file: 'test.md', text: 'This is a test message.'
createGitHubRelease(
        credentialId: 'a1234',
        repository: 'jcustenborder/xjc-kafka-connect-plugin',
        tag: 'v1.2.9',
        commitish: '17b5676aaab28e334c0a9befc86e7615a7539c32',
        bodyFile: 'test.md',
        draft: true
)
```

### listGitHubReleases

Step is used to list the releases in a GitHub repository.

#### List releases that are not drafts

```groovy
def releases = listGitHubReleases(
        credentialId: 'a1234',
        includeDrafts: false,
        repository: 'jcustenborder/xjc-kafka-connect-plugin'
)
if (!releases) {
    throw new Exception("Releases should not be null.")
}
if(releases.size() != 3) {
    throw new Exception("3 Releases should be present.")
}
```

#### Find the latest symantec version and increment it.

```groovy
def releases = listGitHubReleases(
        credentialId: 'a1234',
        repository: 'jcustenborder/xjc-kafka-connect-plugin',
        sortBy: 'SymantecVersion',
        sortAscending: false,
        tagNamePattern: "^0\\.2\\.\\d+"
)
if (!releases) {
    throw new Exception('Releases should not be null.')
}
if(releases.size() != 1) {
    throw new Exception('1 Releases should be present.')
}
def release = releases[0]
def tagVersion = release.tagName

def nextVersion = release.nextSymantecRevision()

def expectedVersion = "0.2.14"

if(nextVersion != expectedVersion) {
    throw new Exception("Expected version is ${expectedVersion} but ${nextVersion} was returned. Input version ${tagVersion}")
}
```

### uploadGithubReleaseAsset

Step is used to upload assets to an existing GitHub repository.

#### Upload assets from the workspace and attach to the GitHub Release

```groovy
uploadGithubReleaseAsset(
        credentialId: 'github-token',
        repository: 'jcustenborder/xjc-kafka-connect-plugin',
        tagName: 'v1.2.9', 
        uploadAssets: [
                [filePath: 'releasenotes.md'], 
                [filePath: 'release.zip']
        ]
)
```