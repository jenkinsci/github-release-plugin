package io.jenkins.plugins.github.release;

import hudson.Util;
import java.io.File;
import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class UploadAsset implements Serializable {
    static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public String contentType = DEFAULT_CONTENT_TYPE;

    @DataBoundConstructor
    public UploadAsset(String filePath) {
        setFilePath(filePath);
    }

    @DataBoundSetter
    public void setContentType(String contentType) {
        String c = Util.fixEmptyAndTrim(contentType);
        this.contentType = null == c ? DEFAULT_CONTENT_TYPE : c;
    }

    public String filePath;

    @DataBoundSetter
    public void setFilePath(String filePath) {
        this.filePath = Util.fixEmptyAndTrim(filePath);
    }

    public File toFile() {
        return new File(this.filePath);
    }

    public boolean isMissing() {
        return !toFile().isFile();
    }
}
