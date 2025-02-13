package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.emptyCollectionOf;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link ClientCredential}.
 *
 */
public class ClientCredentialTest {

    @Test
    public void testBuilder() throws Exception {
        ClientCredential credential = ClientCredential.builder()
                .accessToken("akaa-ATATATATATATATAT-ATATATATATATATAT")
                .clientSecret("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")
                .clientToken("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")
                .host("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")
                .maxBodySize(65)
                .build();
        assertThat(credential.getAccessToken(), is(equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        assertThat(credential.getClientSecret(), is(equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        assertThat(credential.getClientToken(), is(equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        assertThat(credential.getHost(), is(equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        assertThat(credential.getHeadersToSign(), is(emptyCollectionOf(String.class)));
        assertThat(credential.getMaxBodySize(), is(65));
    }

    @Test
    public void testMaxBodySizeDefault() throws Exception {
        ClientCredential credential = ClientCredential.builder()
                .accessToken("akaa-ATATATATATATATAT-ATATATATATATATAT")
                .clientSecret("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")
                .clientToken("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")
                .host("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")
                .build();
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test
    public void testHeaderToSign() throws Exception {
        ClientCredential credential = ClientCredential.builder()
                .accessToken("akaa-ATATATATATATATAT-ATATATATATATATAT")
                .clientSecret("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")
                .clientToken("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")
                .host("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")
                .headerToSign("foo")
                .headerToSign("bar")
                .build();
        assertThat(credential.getHeadersToSign(), hasSize(2));
        assertThat(credential.getHeadersToSign(), containsInAnyOrder("foo", "bar"));
    }

    @Test
    public void testHeaderCaseInsensitive() throws Exception {
        ClientCredential credential = ClientCredential.builder()
                .accessToken("akaa-ATATATATATATATAT-ATATATATATATATAT")
                .clientSecret("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")
                .clientToken("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")
                .host("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")
                .headerToSign("FoO")
                .headerToSign("foo")
                .headerToSign("FOO")
                .headerToSign("bar")
                .build();
        assertThat(credential.getHeadersToSign(), hasSize(2));
        assertThat(credential.getHeadersToSign(), containsInAnyOrder("foo", "bar"));
    }

}
