package com.akamai.edgegrid.signer.apachehttpclient;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Integration tests for {@link ApacheHttpClientEdgeGridInterceptor}.
 */
public class ApacheHttpClientEdgeGridInterceptorIntegrationTest {

    static final String SERVICE_MOCK_HOST = "localhost";

    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicHttpsPort());

    ClientCredential credential;

    private String getHost() {
        return SERVICE_MOCK_HOST + ":" + wireMockServer.httpsPort();
    }

    @BeforeClass
    public void setUp() {
        wireMockServer.start();
        credential = ClientCredential.builder()
                .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
                .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
                .clientSecret("SOMESECRET")
                .host(getHost())
                .build();
    }

    @BeforeMethod
    public void reset() {
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }

    @Test
    public void testInterceptor() throws URISyntaxException, IOException, RequestSigningException {
        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(getHost()))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "/billing-usage/v1/reportSources/alternative")));

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources/alternative"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(getHost()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        HttpGet request = new HttpGet("http://endpoint.net/billing-usage/v1/reportSources");

        HttpClient client = HttpClientSetup.getHttpClientWithRelaxedSsl()
                .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
                .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
                .build();

        client.execute(request);

        List<LoggedRequest> loggedRequests = wireMockServer.findRequestsMatching(RequestPattern
                .everything()).getRequests();

        MatcherAssert.assertThat(loggedRequests.get(0).getHeader("Authorization"),
                Matchers.not(CoreMatchers.equalTo(loggedRequests.get(1).getHeader("Authorization"))));
    }

}
