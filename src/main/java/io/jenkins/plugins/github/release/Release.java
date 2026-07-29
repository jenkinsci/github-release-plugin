package io.jenkins.plugins.github.release;

import java.io.Serializable;
import java.util.StringJoiner;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;
import org.kohsuke.github.GHRelease;

public class Release implements Serializable {
    @Whitelisted
    public final long id;

    @Whitelisted
    public final String htmlUrl;

    @Whitelisted
    public final String assetsUrl;

    @Whitelisted
    public final String uploadUrl;

    @Whitelisted
    public final String tagName;

    @Whitelisted
    public final String targetCommitish;

    @Whitelisted
    public final String name;

    @Whitelisted
    public final String body;

    @Whitelisted
    public final boolean draft;

    @Whitelisted
    public final boolean prerelease;

    @Whitelisted
    public final String tarballUrl;

    @Whitelisted
    public final String zipballUrl;

    @Whitelisted
    public final String discussionUrl;

    public Release(
            long id,
            String htmlUrl,
            String assetsUrl,
            String uploadUrl,
            String tagName,
            String targetCommitish,
            String name,
            String body,
            boolean draft,
            boolean prerelease,
            String tarballUrl,
            String zipballUrl,
            String discussionUrl) {
        this.id = id;
        this.htmlUrl = htmlUrl;
        this.assetsUrl = assetsUrl;
        this.uploadUrl = uploadUrl;
        this.tagName = tagName;
        this.targetCommitish = targetCommitish;
        this.name = name;
        this.body = body;
        this.draft = draft;
        this.prerelease = prerelease;
        this.tarballUrl = tarballUrl;
        this.zipballUrl = zipballUrl;
        this.discussionUrl = discussionUrl;
    }

    Release(String tagName) {
        this(1234, null, null, null, tagName, null, null, null, false, false, null, null, null);
    }

    static Release from(GHRelease release) {
        Release result = new Release(
                release.getId(),
                (null != release.getHtmlUrl() ? release.getHtmlUrl().toString() : null),
                release.getAssetsUrl(),
                release.getUploadUrl(),
                release.getTagName(),
                release.getTargetCommitish(),
                release.getName(),
                release.getBody(),
                release.isDraft(),
                release.isPrerelease(),
                release.getTarballUrl(),
                release.getZipballUrl(),
                release.getDiscussionUrl());

        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Release.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("htmlUrl='" + htmlUrl + "'")
                .add("assetsUrl='" + assetsUrl + "'")
                .add("uploadUrl='" + uploadUrl + "'")
                .add("tagName='" + tagName + "'")
                .add("targetCommitish='" + targetCommitish + "'")
                .add("name='" + name + "'")
                .add("body='" + body + "'")
                .add("draft=" + draft)
                .add("prerelease=" + prerelease)
                .add("tarballUrl='" + tarballUrl + "'")
                .add("zipballUrl='" + zipballUrl + "'")
                .add("discussionUrl='" + discussionUrl + "'")
                .toString();
    }

    private SymantecVersion symantecVersion() {
        if (!SymantecVersion.isSymantecVersion(this.tagName)) {
            throw new IllegalStateException(String.format("TagName '%s' is not a symantec version.", this.tagName));
        }
        return SymantecVersion.of(this.tagName);
    }

    @Whitelisted
    public String nextSymantecRevision() {
        SymantecVersion thisVersion = symantecVersion();
        SymantecVersion nextVersion = thisVersion.incrementRevision();
        return nextVersion.toString();
    }
}
