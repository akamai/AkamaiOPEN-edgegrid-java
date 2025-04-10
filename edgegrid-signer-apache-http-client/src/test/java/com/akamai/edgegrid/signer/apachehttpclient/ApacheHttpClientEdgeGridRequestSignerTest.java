package com.akamai.edgegrid.signer.apachehttpclient;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Example of use of EdgeGrid signer with Apache HTTP Client.
 *
 */
public class ApacheHttpClientEdgeGridRequestSignerTest {

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

    @Test
    public void signEachRequest() throws URISyntaxException, IOException, RequestSigningException {

        HttpRequest request = new HttpGet("https://ignored-hostname.com/billing-usage/v1/reportSources");

        ApacheHttpClientEdgeGridRequestSigner apacheHttpSinger = new ApacheHttpClientEdgeGridRequestSigner(credential);
        apacheHttpSinger.sign(request, request);


        assertThat(URI.create(request.getRequestLine().getUri()).getHost(), equalTo("endpoint.net"));
        assertThat(request.getFirstHeader("Authorization"), notNullValue());
        assertThat(request.getFirstHeader("Authorization").getValue(), not(isEmptyOrNullString()));
    }

}
