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
import hudson.model.Result;
import hudson.util.Secret;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

public class CreateReleaseStepTests extends AbstractWireMockTests {


  @Test
  public void missingCredentialId() throws Exception {
    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    String script = "" +
        "node {" +
        "  createGitHubRelease(tag: 'v1.2.3', commitish: '00000')" +
        "}";

    job.setDefinition(new CpsFlowDefinition(script, true));

    WorkflowRun run = job.scheduleBuild2(0).get();

    j.assertBuildStatus(Result.FAILURE, run);
    j.assertLogContains("credentialId cannot be null", run);
  }

  @Test
  public void executeBodyText() throws Exception {
    SystemCredentialsProvider instance = SystemCredentialsProvider.getInstance();
    instance.getCredentials().add(new StringCredentialsImpl(CredentialsScope.GLOBAL, "a1234", "desc", Secret.fromString("asdfas")));
    instance.save();


    final String script = loadScript("bodyText.groovy");

    // Create a new Pipeline with the given (Scripted Pipeline) definition
    WorkflowJob job = j.createProject(WorkflowJob.class);
    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }


  @Test
  public void executeBodyFile() throws Exception {
    SystemCredentialsProvider instance = SystemCredentialsProvider.getInstance();
    instance.getCredentials().add(new StringCredentialsImpl(CredentialsScope.GLOBAL, "a1234", "desc", Secret.fromString("asdfasd")));
    instance.save();

    final String script = loadScript("bodyFile.groovy");

    WorkflowJob job = j.createProject(WorkflowJob.class);

    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }

  @Test
  public void executeGenerateReleaseNotes() throws Exception {
    SystemCredentialsProvider instance = SystemCredentialsProvider.getInstance();
    instance.getCredentials().add(new StringCredentialsImpl(CredentialsScope.GLOBAL, "a1234", "desc", Secret.fromString("asdfasd")));
    instance.save();

    final String script = loadScript("generateReleaseNotes.groovy");

    WorkflowJob job = j.createProject(WorkflowJob.class);

    job.setDefinition(new CpsFlowDefinition(script, true));
    j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
  }
}
