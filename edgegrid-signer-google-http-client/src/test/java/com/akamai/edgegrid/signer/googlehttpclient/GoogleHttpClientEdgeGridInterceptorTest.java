package com.akamai.edgegrid.signer.googlehttpclient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.testng.annotations.Test;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;

/**
 * Unit tests for {@link GoogleHttpClientEdgeGridInterceptor}.
 *
 */
public class GoogleHttpClientEdgeGridInterceptorTest {

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

    @Test
    public void testInterceptor() throws URISyntaxException, IOException, RequestSigningException {
        HttpRequestFactory requestFactory = createSigningRequestFactory();

        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        // Mimic what the library does to process the interceptor.
        request.getInterceptor().intercept(request);

        assertThat(request.getHeaders().containsKey("host"), is(false));
        assertThat(request.getUrl().getHost(), equalTo("endpoint.net"));
        assertThat(request.getHeaders().getAuthorization(), not(isEmptyOrNullString()));
    }

    @Test
    public void testInterceptorWithHeader() throws URISyntaxException, IOException, RequestSigningException {
        HttpRequestFactory requestFactory = createSigningRequestFactory();

        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        request.getHeaders().put("Host", "ignored-hostname.com");
        // Mimic what the library does to process the interceptor.
        request.getInterceptor().intercept(request);

        assertThat(request.getHeaders().containsKey("Host"), is(false));
        assertThat(request.getHeaders().containsKey("host"), is(true));
        assertThat((String) request.getHeaders().get("host"), equalTo("endpoint.net"));
        assertThat(request.getUrl().getHost(), equalTo("endpoint.net"));
        assertThat(request.getHeaders().getAuthorization(), not(isEmptyOrNullString()));
    }

    private HttpRequestFactory createSigningRequestFactory() {
        HttpClient client = ApacheHttpTransport.newDefaultHttpClientBuilder()
                .setSSLSocketFactory((SSLSocketFactory.getSystemSocketFactory()))
                .build();

        return new ApacheHttpTransport(client).createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setInterceptor(new GoogleHttpClientEdgeGridInterceptor(credential));
            }
        });
    }
}
