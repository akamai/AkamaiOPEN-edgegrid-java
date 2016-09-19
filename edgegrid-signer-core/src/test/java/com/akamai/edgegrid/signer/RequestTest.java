package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class RequestTest {

    @Test
    public void basicTest() throws RequestSigningException {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUriWithQuery(), equalTo(URI.create("http://control.akamai.com/check")));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test(expectedExceptions = RequestSigningException.class)
    public void testRejectDuplicateHeaderNames() throws RequestSigningException {
        Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("Duplicate", "Y")
                .build();
    }

    @Test(expectedExceptions = RequestSigningException.class)
    public void testRejectDuplicateHeaderNamesMap() throws RequestSigningException {
        Request.RequestBuilder builder = Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X");
        Map<String, String> headers = new HashMap<>();
        headers.put("Duplicate", "y");
        builder.headers(headers);
    }

    @Test(expectedExceptions = RequestSigningException.class)
    public void testRejectDuplicateHeaderNamesMixedCase() throws RequestSigningException {
        Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("DUPLICATE", "Y")
                .build();
    }

}
