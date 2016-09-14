package com.akamai.edgegrid.signer;

import java.io.InputStream;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.akamai.edgegrid.signer.ClientCredential;

public class ClientCredentialTest {

    @Test
    public void testGood1() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good1");
        MatcherAssert.assertThat(credential.getAccessToken(), Matchers.is(Matchers.equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        MatcherAssert.assertThat(credential.getClientSecret(), Matchers.is(Matchers.equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        MatcherAssert.assertThat(credential.getClientToken(), Matchers.is(Matchers.equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        MatcherAssert.assertThat(credential.getHost(), Matchers.is(Matchers.equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        MatcherAssert.assertThat(credential.getHeadersToSign(), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(credential.getMaxBodySize(), Matchers.is(65536));
    }

    @Test
    public void testGood2() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good2");
        MatcherAssert.assertThat(credential.getAccessToken(), Matchers.is(Matchers.equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        MatcherAssert.assertThat(credential.getClientSecret(), Matchers.is(Matchers.equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        MatcherAssert.assertThat(credential.getClientToken(), Matchers.is(Matchers.equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        MatcherAssert.assertThat(credential.getHost(), Matchers.is(Matchers.equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        MatcherAssert.assertThat(credential.getHeadersToSign(), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(credential.getMaxBodySize(), Matchers.is(131072));
    }

    @Test
    public void testGood3() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good3");
        MatcherAssert.assertThat(credential.getAccessToken(), Matchers.is(Matchers.equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        MatcherAssert.assertThat(credential.getClientSecret(), Matchers.is(Matchers.equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        MatcherAssert.assertThat(credential.getClientToken(), Matchers.is(Matchers.equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        MatcherAssert.assertThat(credential.getHost(), Matchers.is(Matchers.equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        MatcherAssert.assertThat(credential.getHeadersToSign(), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(credential.getMaxBodySize(), Matchers.is(131072));
    }

    @Test
    public void testGood4() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good4");
        MatcherAssert.assertThat(credential.getAccessToken(), Matchers.is(Matchers.equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        MatcherAssert.assertThat(credential.getClientSecret(), Matchers.is(Matchers.equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        MatcherAssert.assertThat(credential.getClientToken(), Matchers.is(Matchers.equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        MatcherAssert.assertThat(credential.getHost(), Matchers.is(Matchers.equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        MatcherAssert.assertThat(credential.getHeadersToSign(), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(credential.getMaxBodySize(), Matchers.is(131072));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testBad1() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential.fromEdgeRc(inputStream, "bad1");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testBad2() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential.fromEdgeRc(inputStream, "bad2");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testBad3() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential.fromEdgeRc(inputStream, "bad3");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testBad4() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential.fromEdgeRc(inputStream, "bad4");
    }

}
