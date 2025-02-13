package com.akamai.edgegrid.signer.restassured;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.EdgeGridV1Signer;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Unit tests for {@link RestAssuredEdgeGridFilterIntegrationTest}.
 *
 */
public class RestAssuredEdgeGridFilterIntegrationTest {

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
    // Due to limitations of REST-assured we cannot sign again followed redirects
    // https://github.com/akamai-open/AkamaiOPEN-edgegrid-java/issues/21
    public void cannotSignAgainFollowedRedirects() throws URISyntaxException, IOException {

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

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);

        List<LoggedRequest> loggedRequests = wireMockServer.findRequestsMatching(RequestPattern
                .everything()).getRequests();
        MatcherAssert.assertThat(loggedRequests.get(0).getHeader("Authorization"),
                CoreMatchers.equalTo(loggedRequests.get(1).getHeader("Authorization")));
    }

    @Test
    public void signEachRequest() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);

        assertThat(wireMockServer.findAllUnmatchedRequests().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void signEachRequestWithPathParams() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathMatching("/config-gtm/v1/domains/.*"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("/config-gtm/v1/domains/{domain}", "storage1.akadns.net")
                .then().statusCode(200);

        assertThat(wireMockServer.findAllUnmatchedRequests().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void signEachRequestWithPathParamsAndQueryString() throws URISyntaxException,
            IOException {

        wireMockServer.stubFor(get(urlPathMatching("/config-gtm/v1/domains/.*"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .withQueryParam("param1", equalTo("value1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .queryParam("param1", "value1")
                .get("/config-gtm/v1/domains/{domain}", "storage1.akadns.net")
                .then().statusCode(200);

        assertThat(wireMockServer.findAllUnmatchedRequests().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void signWithHostHeader() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .header("Host", "ignored-hostname.com")
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);

        assertThat(wireMockServer.findAllUnmatchedRequests().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void replacesProvidedHostHeader() throws URISyntaxException, IOException,
            RequestSigningException {


        RestAssured.given()
                .relaxedHTTPSValidation()
                .header("Host", "ignored-hostname.com")
                .filter(new RestAssuredEdgeGridFilter(credential))
                .filter(new Filter() {
                    @Override
                    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                        MatcherAssert.assertThat(requestSpec.getHeaders().getList("Host").size(),
                                CoreMatchers.equalTo(1));
                        MatcherAssert.assertThat(requestSpec.getHeaders().get("Host").getValue(),
                                CoreMatchers.equalTo(credential.getHost()));

                        return ctx.next(requestSpec, responseSpec);
                    }
                })
                .get();


    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignEachRequestWithAbsolutePath() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("https://ignored-hostname.com/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignRequestWithFileContent() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .body(new File("/home/johan/some_large_file.bin"))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignRequestWithInputStreamContent() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .body(new ByteArrayInputStream("exampleString".getBytes(StandardCharsets.UTF_8)))
                .post("/billing-usage/v1/reportSources")
        .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignRequestWithMultipartContent() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .multiPart("file", new File("/home/johan/some_large_file.bin"))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    class MockedEdgeGridV1Signer extends EdgeGridV1Signer {
        String fixedNonce = "ec9d20ee-1e9b-4c1f-925a-f0017754f86c";
        // Fixed timestamp corresponds to 2016-08-04T07:00:00+0000.
        long fixedTimestamp = 1470294000000L;

        protected long getTimestamp() {
            return fixedTimestamp;
        }

        protected String getNonce() {
            return fixedNonce;
        }

    }

    class MockedRestAssuredEdgeGridRequestSigner extends RestAssuredEdgeGridRequestSigner {

        public MockedRestAssuredEdgeGridRequestSigner(ClientCredential clientCredential) {
            super(clientCredential);
        }

        @Override
        protected EdgeGridV1Signer createEdgeGridSigner() {
            return new MockedEdgeGridV1Signer();
        }
    }

    class MockedRestAssuredEdgeGridFilter extends RestAssuredEdgeGridFilter {

        public MockedRestAssuredEdgeGridFilter(ClientCredential credential) {
            super(credential);
            this.binding = new MockedRestAssuredEdgeGridRequestSigner(credential);
        }
    }
    @Test
    public void signRequestWithPathParamContainingURL() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathMatching("/sso-config/v1/idps/https%3A%2F%2Ffdef2ea8-64b1-4b78-ad36-bacae87af167/certificates"))
                .withHeader("Authorization", equalTo("EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=2KunLDWST5ZgrbL8CuTF2Gxp7UfsIy/DxELcajvziTo="))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssuredEdgeGridFilter filter = new MockedRestAssuredEdgeGridFilter(credential);
        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(filter)
                .get("/sso-config/v1/idps/{id}/certificates", "https://fdef2ea8-64b1-4b78-ad36-bacae87af167")
                .then().statusCode(200);

        assertThat(wireMockServer.findAllUnmatchedRequests().size(), CoreMatchers.equalTo(0));
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }

}
