package io.jenkins.plugins.github.release;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import hudson.util.Secret;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.security.Security;

public class UploadReleaseAssetStepTests extends AbstractWireMockTests {

    @BeforeClass
    public static void setupSecurityProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Before
    public void setupCredentials() throws IOException {
        final SystemCredentialsProvider instance = SystemCredentialsProvider.getInstance();
        instance.getCredentials().add(new StringCredentialsImpl(CredentialsScope.GLOBAL, "a1234", "desc", Secret.fromString("adfadasdafdsa")));
        instance.save();
    }

    @Test
    public void defaultOverwriteFalseUploadsNormally() throws Exception {
        final String script = loadScript("defaultOverwriteFalseUploadsNormally.groovy");

        final WorkflowJob job = j.createProject(WorkflowJob.class);
        job.setDefinition(new CpsFlowDefinition(script, true));
        j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
    }

    @Test
    public void overwriteTrueDeletesMatchingAssetBeforeUploading() throws Exception {
        final String script = loadScript("overwriteTrueDeletesMatchingAssetBeforeUploading.groovy");

        githubApi.stubFor(get(urlPathMatching("/repos/jcustenborder/xjc-kafka-connect-plugin/releases/125020531/assets"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"url\":\"https://api.github.com/repos/jcustenborder/xjc-kafka-connect-plugin/releases/assets/525344122\",\"id\":525344122,\"node_id\":\"RA_kwDONpmRNc4fUB16\",\"name\":\"app.jar\",\"label\":\"\",\"uploader\":{\"login\":\"github-actions[bot]\",\"id\":41898282,\"node_id\":\"MDM6Qm90NDE4OTgyODI=\",\"avatar_url\":\"https://avatars.githubusercontent.com/in/15368?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/github-actions%5Bbot%5D\",\"html_url\":\"https://github.com/apps/github-actions\",\"followers_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/followers\",\"following_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/subscriptions\",\"organizations_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/orgs\",\"repos_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/repos\",\"events_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/github-actions%5Bbot%5D/received_events\",\"type\":\"Bot\",\"user_view_type\":\"public\",\"site_admin\":false},\"content_type\":\"application/zip\",\"state\":\"uploaded\",\"size\":242813,\"digest\":\"sha256:09d041843e1ae75842d91dcc7c2401b6d6a5afef681f15f9a9b422f496d20383\",\"download_count\":0,\"created_at\":\"2026-08-22T18:55:41Z\",\"updated_at\":\"2026-08-22T18:55:41Z\",\"browser_download_url\":\"https://github.com/jcustenborder/xjc-kafka-connect-plugin/releases/download/v1.2.3/app.jar\"}]")));

        githubApi.stubFor(delete(urlPathMatching("/repos/.*/assets/525344122"))
                .willReturn(aResponse().withStatus(204)));

        final WorkflowJob job = j.createProject(WorkflowJob.class);
        job.setDefinition(new CpsFlowDefinition(script, true));
        j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
    }

    @Test
    public void overwriteTrueNoMatchingAssetProceedsWithoutDeletion() throws Exception {
        final String script = loadScript("overwriteTrueNoMatchingAssetProceedsWithoutDeletion.groovy");

        final WorkflowJob job = j.createProject(WorkflowJob.class);
        job.setDefinition(new CpsFlowDefinition(script, true));
        j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
    }
}