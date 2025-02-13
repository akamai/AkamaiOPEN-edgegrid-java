package com.akamai.edgegrid.signer.googlehttpclient;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


/**
 * Integration tests for {@link GoogleHttpClientEdgeGridInterceptor}.
 *
 */
public class GoogleHttpClientEdgeGridInterceptorIntegrationTest {

    static final String SERVICE_MOCK_HOST = "localhost";
    static final int SERVICE_MOCK_PORT = 9089;
    static final String SERVICE_MOCK = SERVICE_MOCK_HOST + ":" + SERVICE_MOCK_PORT;

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host(SERVICE_MOCK)
            .build();

    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().httpsPort(SERVICE_MOCK_PORT));

    @BeforeClass
    public void setUp() {
        wireMockServer.start();
    }

    @BeforeMethod
    public void reset() {
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
    }

    @Test
    public void testInterceptor() throws URISyntaxException, IOException, RequestSigningException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "/billing-usage/v1/reportSources/alternative")));

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources/alternative"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        HttpRequestFactory requestFactory = createSigningRequestFactory();

        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        // Mimic what the library does to process the interceptor.
        request.setFollowRedirects(true).execute();

        List<LoggedRequest> loggedRequests = wireMockServer.findRequestsMatching(RequestPattern
                .everything()).getRequests();
        MatcherAssert.assertThat(loggedRequests.get(0).getHeader("Authorization"),
                Matchers.not(CoreMatchers.equalTo(loggedRequests.get(1).getHeader("Authorization"))));

    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }

    private HttpRequestFactory createSigningRequestFactory() {
        HttpTransport httpTransport = null;
        try {
            httpTransport = new ApacheHttpTransport.Builder().doNotValidateCertificate().build();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return httpTransport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setInterceptor(new GoogleHttpClientEdgeGridInterceptor(credential));
            }
        });
    }

}
