package io.jenkins.plugins.github.release;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.interceptor.RequirePOST;

public abstract class AbstractReleaseDescriptor extends StepDescriptor {
  @RequirePOST
  public ListBoxModel doFillCredentialIdItems(@AncestorInPath Item context) {


    if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER) ||
        context != null && !context.hasPermission(Item.EXTENDED_READ)) {
      return new StandardListBoxModel();
    }

    return new StandardListBoxModel()
        .includeEmptyValue()
        .includeAs(ACL.SYSTEM, context, StringCredentials.class)
        .includeAs(ACL.SYSTEM, context, StandardUsernamePasswordCredentials.class);
  }

}
