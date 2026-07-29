# Introduction

The GitHub Release plugin provides pipeline steps to create and query GitHub releases.

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

#### Create a release with auto-generated release notes

When `generateReleaseNotes` is set to `true`, GitHub automatically generates
release notes from merged pull requests since the last published release.

```groovy
createGitHubRelease(
        credentialId: 'a1234',
        repository: 'jcustenborder/xjc-kafka-connect-plugin',
        tag: 'v1.3.0',
        commitish: '17b5676aaab28e334c0a9befc86e7615a7539c32',
        generateReleaseNotes: true
)
```

If `bodyText` or `bodyFile` is also specified, that text will be prepended to the
automatically generated notes.

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