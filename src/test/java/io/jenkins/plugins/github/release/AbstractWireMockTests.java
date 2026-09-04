package io.jenkins.plugins.github.release;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.stream.Collectors;

public class AbstractWireMockTests {
  protected final String baseFilesClassPath = this.getClass().getName().replace('.', '/');
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
          .extensions(
              new ResponseTransformer() {
                @Override
                public Response transform(final Request request, final Response response, final FileSource files, final Parameters parameters) {
                  try {
                    if (response.getHeaders() != null &&
                            response.getHeaders().getContentTypeHeader() != null &&
                            "application/json".equals(response.getHeaders().getContentTypeHeader().mimeTypePart())) {

                      // Dynamically extract host and port from incoming request header
                      final URI requestUri = URI.create(request.getAbsoluteUrl());
                      final String currentHost = requestUri.getScheme() + "://" + requestUri.getAuthority();

                      final String body = response.getBodyAsString();
                      if (body != null) {
                        // Replaces both api.github.com and uploads.github.com URLs with local WireMock host:port
                        final String modifiedBody = body
                                .replace("https://api.github.com", currentHost)
                                .replace("https://raw.githubusercontent.com", currentHost)
                                .replace("https://uploads.github.com", currentHost);

                        return Response.Builder.like(response)
                                .but()
                                .body(modifiedBody)
                                .build();
                      }
                    }
                  } catch (final Exception e) {
                    // Return unmodified response if transform fails
                  }
                  return response;
                }

                @Override
                public String getName() {
                  return "url-rewrite";
                }

              })
  );

  @Rule
  public JenkinsRule j = new JenkinsRule();

  protected String loadScript(final String name) throws IOException {

    final String path = "/" + baseFilesClassPath + "/" + name;

    final String script;
    try (final InputStream inputStream = this.getClass().getResourceAsStream(path)) {
      if (null == inputStream) {
        throw new FileNotFoundException(
            path
        );
      }

      try (final Reader inputStreamReader = new InputStreamReader(inputStream)) {
        try (final BufferedReader reader = new BufferedReader(inputStreamReader)) {
          script = reader.lines().collect(Collectors.joining("\n"));
        }
      }
    }

    return String.format(script, githubServer());
  }
}
