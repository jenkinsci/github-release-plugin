package io.jenkins.plugins.github.release;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

public class AbstractWireMockTests {
    protected final String baseFilesClassPath = this.getClass().getName().replace('.', '/');
    ;
    protected final String baseRecordPath = "src/test/resources/" + baseFilesClassPath;

    protected String githubServer() {
        //    return "http://localhost:8080/";
        return githubApi.baseUrl();
    }

    public WireMockRuleFactory factory = new WireMockRuleFactory();

    @Rule
    public WireMockRule githubApi = factory.getRule(WireMockConfiguration.options()
            .dynamicPort()
            .usingFilesUnderClasspath(baseFilesClassPath + "/api")
            .extensions(new ResponseTransformer() {
                @Override
                public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
                    try {
                        if ("application/json"
                                .equals(response.getHeaders()
                                        .getContentTypeHeader()
                                        .mimeTypePart())) {
                            // Something strange happending here... turning off for now
                            //                        return Response.Builder.like(response)
                            //                            .but()
                            //                            .body(response.getBodyAsString()
                            //                                .replace("https://api.github.com/",
                            //                                    "http://localhost:" + githubApi.port() + "/")
                            //                                .replace("https://raw.githubusercontent.com/",
                            //                                    "http://localhost:" + githubRaw.port() + "/")
                            //                            )
                            //                            .build();
                        }
                    } catch (Exception e) {
                    }
                    return response;
                }

                @Override
                public String getName() {
                    return "url-rewrite";
                }
            }));

    @Rule
    public JenkinsRule j = new JenkinsRule();

    protected String loadScript(String name) throws IOException {

        String path = "/" + baseFilesClassPath + "/" + name;

        String script;
        try (InputStream inputStream = this.getClass().getResourceAsStream(path)) {
            if (null == inputStream) {
                throw new FileNotFoundException(path);
            }

            try (Reader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    script = reader.lines().collect(Collectors.joining("\n"));
                }
            }
        }

        return String.format(script, githubServer());
    }
}
