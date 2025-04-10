package com.akamai.edgegrid.signer.ahc;

import com.akamai.edgegrid.signer.ClientCredential;

import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;


public class AsyncHttpClientEdgeGridSignatureCalculatorTest {

    @Test(dataProvider = "requests")
    public void testCalculateAndAddSignatureForGet(Request request) throws Exception {

        ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

        RequestBuilder requestToUpdate = new RequestBuilder(request.toString());
        new AsyncHttpClientEdgeGridSignatureCalculator(credential).calculateAndAddSignature(
            request, requestToUpdate);
        Request updatedRequest = requestToUpdate.build();

        assertThat(updatedRequest.getHeaders().get("Authorization"), not(isEmptyOrNullString()));
        assertThat(updatedRequest.getHeaders().get("Host"), equalTo("endpoint.net"));
        assertThat(updatedRequest.getUri().getHost(), equalTo("endpoint.net"));

    }

    @Test
    public void testPreservingQueryString() throws Exception {

        ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

        Request request = new RequestBuilder().setUrl("http://localhost/test?x=y").build();
        RequestBuilder requestToUpdate = new RequestBuilder(request.toString());

        new AsyncHttpClientEdgeGridSignatureCalculator(credential).calculateAndAddSignature(
            request, requestToUpdate);
        Request updatedRequest = requestToUpdate.build();

        assertThat(updatedRequest.getUri().getQuery(), equalTo("x=y"));

    }

    @Test
    public void testNotDuplicatingQueryString() throws Exception {

        ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

        Request request = new RequestBuilder().setUrl("http://localhost/test").addQueryParam("x", "y").build();
        RequestBuilder requestToUpdate = new RequestBuilder(request.toString());

        new AsyncHttpClientEdgeGridSignatureCalculator(credential).calculateAndAddSignature(
            request, requestToUpdate);
        Request updatedRequest = requestToUpdate.build();

        assertThat(updatedRequest.getUri().getQuery(), equalTo("x=y"));

    }

    @DataProvider
    public Object[][] requests() {
        return new Object[][]{
            {new RequestBuilder().setUrl("http://localhost/test").build()},
            {new RequestBuilder("POST").setUrl("http://localhost/test").setBody("content").build()},
            {new RequestBuilder("POST").setUrl("http://localhost/test").setBody("content".getBytes()).build()},
            {new RequestBuilder("POST").setUrl("http://localhost/test").setBody(Arrays.asList("content".getBytes())).build()}
        };
    }
}
