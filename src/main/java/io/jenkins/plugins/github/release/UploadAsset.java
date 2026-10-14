package io.jenkins.plugins.github.release;

import hudson.FilePath;
import hudson.Util;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

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

  public InputStream toStream(FilePath workspace) throws IOException, InterruptedException {
    return workspace.child(filePath).read();
  }


  public boolean isMissing(FilePath workspace) {
    try {
      FilePath file = workspace.child(this.filePath);
      return !file.exists() || file.isDirectory();
    } catch (IOException | InterruptedException e) {
      // If an exception occurs, assume the file is missing
      return true;
    }
  }
}
