package com.akamai.edgegrid.signer.apachehttpclient5;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.testng.annotations.Test;

import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Example of use of EdgeGrid signer with Apache HTTP Client5.
 */
public class ApacheHttpClient5EdgeGridRequestSignerTest {

    private static final ClientCredential CREDENTIAL = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

    @Test
    public void signEachRequest() throws URISyntaxException, RequestSigningException {
        var request = new HttpGet("https://ignored-hostname.com/billing-usage/v1/reportSources");

        var apacheHttpSinger = new ApacheHttpClient5EdgeGridRequestSigner(CREDENTIAL);
        apacheHttpSinger.sign(request, request);

        assertThat(request.getUri().getHost(), equalTo("endpoint.net"));
        assertThat(request.getFirstHeader("Authorization"), notNullValue());
        assertThat(request.getFirstHeader("Authorization").getValue(), not(isEmptyOrNullString()));
    }
}
