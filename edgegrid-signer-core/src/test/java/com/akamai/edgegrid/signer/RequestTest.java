package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link Request}.
 *
 */
public class RequestTest {

    @Test(dataProvider = "absoluteUriTestData")
    public void testAcceptRequestWithAbsoluteUriAsString(
            String caseName,
            String uri,
            String expectedPath,
            String expectedQuery) {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(uri)
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUri().getPath(), equalTo(expectedPath));
        assertThat(request.getUri().getQuery(), equalTo(expectedQuery));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test(dataProvider = "absoluteUriTestData")
    public void testAcceptRequestWithAbsoluteUriAsURI(
            String caseName,
            String uri,
            String expectedPath,
            String expectedQuery) {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(URI.create(uri))
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUri().getPath(), equalTo(expectedPath));
        assertThat(request.getUri().getQuery(), equalTo(expectedQuery));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test
    public void testAcceptRequestWithRelativeUri() {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(URI.create("/check"))
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUri(), equalTo(URI.create("/check")));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test
    public void testHeadersLowercasing()  {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(URI.create("/check"))
                .header("HeaDer", "h")
                .build();

        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test
    public void testAcceptRequestWithEmptyRequestBody()  {
        Request request = Request.builder()
                .body("".getBytes())
                .method("GET")
                .uri(URI.create("/check"))
                .header("HeaDer", "h")
                .build();

        assertThat(request.getBody(), equalTo(new byte[]{}));
    }

    @Test
    public void testAcceptRequestWithNullRequestBody()  {
        Request request = Request.builder()
                .method("GET")
                .uri(URI.create("/check"))
                .header("HeaDer", "h")
                .build();

        assertThat(request.getBody(), equalTo(new byte[]{}));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateHeaderNames() {
        Request.builder()
                .method("GET")
                .uri(URI.create("https://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("Duplicate", "Y")
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateCaseInsensitiveHeaderNames() {
        Request.builder()
                .method("GET")
                .uri(URI.create("https://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("DUPLICATE", "Y")
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateHeaderNamesMap() {
        Request.RequestBuilder builder = Request.builder()
                .method("GET")
                .uri(URI.create("https://control.akamai.com/check"))
                .header("Duplicate", "X");
        Map<String, String> headers = new HashMap<>();
        headers.put("Duplicate", "y");
        builder.headers(headers);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateHeaderNamesMixedCase() {
        Request.builder()
                .method("GET")
                .uri(URI.create("https://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("DUPLICATE", "Y")
                .build();
    }

    @DataProvider
    Object[][] absoluteUriTestData() {
        return new Object[][] {
                {"http/yes/yes", "http://anything.com/foo.html?a=b&c=d", "/foo.html", "a=b&c=d"},
                {"http/yes/no", "http://anything.com/bar.html", "/bar.html", null},
                {"http/no/yes", "http://anything.com?a=b", "", "a=b"},
                {"http/no/no", "http://anything.com", "", null},
                {"https/yes/yes", "https://anything.com/foo.html?a=b&c=d", "/foo.html", "a=b&c=d"},
                {"https/yes/no", "https://anything.com/bar.html", "/bar.html", null},
                {"https/no/yes", "https://anything.com?a=b", "", "a=b"},
                {"https/no/no", "https://anything.com", "", null},
        };
    }

}
