package com.akamai.edgegrid.signer.ahc;

import com.akamai.edgegrid.signer.ClientCredential;
import com.github.tomakehurst.wiremock.WireMockServer;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.hamcrest.MatcherAssert.assertThat;

public class AsyncHttpClientEdgeGridRequestSignerIntegrationTest {

    static final String SERVICE_MOCK_HOST = "localhost";
    static final int SERVICE_MOCK_PORT = 9089;
    static final String SERVICE_MOCK = SERVICE_MOCK_HOST + ":" + SERVICE_MOCK_PORT;

    ClientCredential credential = ClientCredential.builder()
        .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
        .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
        .clientSecret("SOMESECRET")
        .host(SERVICE_MOCK)
        .build();

    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(SERVICE_MOCK_PORT));

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
    public void signEachRequest() throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        wireMockServer.stubFor(post(urlPathEqualTo("/papi/v0/properties"))
            .withHeader("Authorization", matching(".*"))
            .withHeader("Host", equalTo(SERVICE_MOCK))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "text/xml")
                .withBody("<response>Some content</response>")));

        Request request = new RequestBuilder("POST")
            .setUrl("http://" + credential.getHost() + "/papi/v0/properties")
            .addQueryParam("contractId","ctr_1-3CV382")
            .addQueryParam("groupId","grp_18385")
            .setBody("{ \"productId\": \"Site_Accel\", \"propertyName\": \"8LuWyUjwea\" }")
            .setHeader("Content-Type", "application/json")
            .setSignatureCalculator(new AsyncHttpClientEdgeGridSignatureCalculator(credential))
            .build();

        asyncHttpClient().executeRequest(request).get();

        assertThat(wireMockServer.findAllUnmatchedRequests().size(), CoreMatchers.equalTo(0));
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }
}
