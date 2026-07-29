package io.jenkins.plugins.github.release;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class WireMockRuleFactory {
    private String urlToMock = System.getProperty("wiremock.record");

    public WireMockRule getRule(int port) {
        return getRule(wireMockConfig().port(port));
    }

    public WireMockRule getRule(Options options) {
        if (urlToMock != null && !urlToMock.isEmpty()) {
            return new WireMockRecorderRule(options, urlToMock);
        } else {
            return new WireMockRule(options);
        }
    }

    private class WireMockRecorderRule extends WireMockRule {
        // needed for WireMockRule file location
        private String mappingLocation = "src/test/resources";

        public WireMockRecorderRule(Options options, String url) {
            super(options);
            this.stubFor(
                    get(urlMatching(".*")).atPriority(10).willReturn(aResponse().proxiedFrom(url)));
            this.enableRecordMappings(
                    new SingleRootFileSource(mappingLocation + "/mappings"),
                    new SingleRootFileSource(mappingLocation + "/__files"));
        }
    }
}
