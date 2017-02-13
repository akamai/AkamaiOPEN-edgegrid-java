package apachehttpclient;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class RestAssuredIntegrationTest {

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
    public void signAgainFollowedRedirects() throws URISyntaxException, IOException {

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

        getBaseRequestSpecification()
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);

        List<LoggedRequest> loggedRequests = wireMockServer.findRequestsMatching(RequestPattern
                .everything()).getRequests();
        MatcherAssert.assertThat(loggedRequests.get(0).getHeader("Authorization"),
                Matchers.not(CoreMatchers.equalTo(loggedRequests.get(1).getHeader("Authorization"))));
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

        getBaseRequestSpecification()
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);
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

        getBaseRequestSpecification()
                .get("/config-gtm/v1/domains/{domain}", "storage1.akadns.net")
                .then().statusCode(200);
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

        getBaseRequestSpecification()
                .queryParam("param1", "value1")
                .get("/config-gtm/v1/domains/{domain}", "storage1.akadns.net")
                .then().statusCode(200);
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

        getBaseRequestSpecification()
                .header("Host", "ignored-hostname.com")
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test
    public void replaceProvidedHostHeaderOnlyInApacheClient() throws URISyntaxException, IOException,
            RequestSigningException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)));

        getBaseRequestSpecification()
                .header("Host", "ignored-hostname.com")
                .filter(new Filter() {
                    @Override
                    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                        MatcherAssert.assertThat(requestSpec.getHeaders().getList("Host").size(),
                                CoreMatchers.equalTo(1));
                        MatcherAssert.assertThat(requestSpec.getHeaders().get("Host").getValue(),
                                CoreMatchers.equalTo("ignored-hostname.com"));
                        return ctx.next(requestSpec, responseSpec);
                    }
                })
                .get("/billing-usage/v1/reportSources");


        List<LoggedRequest> loggedRequests = wireMockServer.findRequestsMatching(RequestPattern
                .everything()).getRequests();
        MatcherAssert.assertThat(loggedRequests.get(0).getHeader("Host"),
                CoreMatchers.equalTo(SERVICE_MOCK));

    }

    @Test
    public void signEachRequestWithAbsolutePath() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)));

        getBaseRequestSpecification()
                .get("https://" + SERVICE_MOCK+ "/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test
    public void signRequestWithFileContent() throws URISyntaxException, IOException {

        wireMockServer.stubFor(post(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)));

        getBaseRequestSpecification()
                .body(new File("./pom.xml"))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test
    public void signRequestWithInputStreamContent() throws URISyntaxException, IOException {

        wireMockServer.stubFor(post(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)));

        getBaseRequestSpecification()
                .body(new ByteArrayInputStream("exampleString".getBytes(StandardCharsets.UTF_8)))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void dontSignRequestWithMultipartContent() throws URISyntaxException, IOException {

        getBaseRequestSpecification()
                .multiPart("file", new File("/home/johan/some_large_file.bin"))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }

    private RequestSpecification getBaseRequestSpecification() {
        return RestAssured.given()
                .config(getRestAssuredConfig(credential))
                .relaxedHTTPSValidation()
                .baseUri("https://" + SERVICE_MOCK);
    }


    private static RestAssuredConfig getRestAssuredConfig(final ClientCredential credential) {
        return RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().httpClientFactory(new HttpClientConfig.HttpClientFactory() {
            @Override
            public HttpClient createHttpClient() {
                final DefaultHttpClient client = new DefaultHttpClient();
                client.addRequestInterceptor(new ApacheHttpClientEdgeGridInterceptor(credential));
                client.setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential));
                return client;
            }
        }));
    }

}
