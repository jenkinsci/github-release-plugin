/*
 * The MIT License
 *
 * Copyright (c) 2016 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package io.jenkins.plugins.github.release;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.util.Secret;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ListReleaseStepTests extends AbstractWireMockTests {





  @Before
  public void setupCredentials() throws IOException {
    SystemCredentialsProvider instance = SystemCredentialsProvider.getInstance();
    instance.getCredentials().add(new StringCredentialsImpl(CredentialsScope.GLOBAL, "a1234", "desc", Secret.fromString("adfadasdafdsa")));
    instance.save();
  }

  @Test
  public void listAllReleases() throws Exception {
    final String script = loadScript("listAllReleases.groovy");

    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }
  @Test
  public void noDrafts() throws Exception {
    final String script = loadScript("noDrafts.groovy");

    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }
  @Test
  public void allDrafts() throws Exception {
    final String script = loadScript("allDrafts.groovy");

    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }
  @Test
  public void tagPattern() throws Exception {
    final String script = loadScript("tagPattern.groovy");

    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }

  @Test
  public void latestSymantecVersion() throws Exception {
    final String script = loadScript("latestSymantecVersion.groovy");

    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());

  }

  @Test
  public void listAllReleasesWithGitHubAppCredential() throws Exception {
    SystemCredentialsProvider instance = SystemCredentialsProvider.getInstance();
    instance.getCredentials().add(new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "app-credential", "desc", "12345", "installation-token"));
    instance.save();

    final String script = loadScript("listAllReleasesGitHubApp.groovy");

    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }

}
