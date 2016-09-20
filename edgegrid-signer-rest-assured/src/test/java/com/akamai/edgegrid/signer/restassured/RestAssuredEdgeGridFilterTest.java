package com.akamai.edgegrid.signer.restassured;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.RequestSigningException;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;

import org.testng.annotations.*;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


public class RestAssuredEdgeGridFilterTest {

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
    public void signEachRequest() throws URISyntaxException, IOException, RequestSigningException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .baseUri("https://ignored-hostname.com")
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test
    public void signWithHostHeader() throws URISyntaxException, IOException, RequestSigningException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .header("Host", "ignored-hostname.com")
                .baseUri("https://ignored-hostname.com")
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignEachRequestWithoutBaseUri() throws URISyntaxException, IOException, RequestSigningException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("https://ignored-hostname.com/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }

}
