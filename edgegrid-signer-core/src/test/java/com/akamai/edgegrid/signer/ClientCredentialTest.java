package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ClientCredentialTest {

    @Test
    public void testGood1() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good1");
        assertThat(credential.getAccessToken(), is(equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        assertThat(credential.getClientSecret(), is(equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        assertThat(credential.getClientToken(), is(equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        assertThat(credential.getHost(), is(equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(65536));
    }

    @Test
    public void testGood2() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good2");
        assertThat(credential.getAccessToken(), is(equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        assertThat(credential.getClientSecret(), is(equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        assertThat(credential.getClientToken(), is(equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        assertThat(credential.getHost(), is(equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test
    public void testGood3() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good3");
        assertThat(credential.getAccessToken(), is(equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        assertThat(credential.getClientSecret(), is(equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        assertThat(credential.getClientToken(), is(equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        assertThat(credential.getHost(), is(equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test
    public void testGood4() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = ClientCredential.fromEdgeRc(inputStream, "good4");
        assertThat(credential.getAccessToken(), is(equalTo("akaa-ATATATATATATATAT-ATATATATATATATAT")));
        assertThat(credential.getClientSecret(), is(equalTo("CSCSCSC+SCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCSCS=")));
        assertThat(credential.getClientToken(), is(equalTo("akaa-CTCTCTCTCTCTCTCT-CTCTCTCTCTCTCTCT")));
        assertThat(credential.getHost(), is(equalTo("akaa-4AAAAAAAAAAAAAAA-AAAAAAAAAAAAAAAA.luna.akamaiapis.net")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testMalformedEdgeRc() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc_malformed");
        ClientCredential.fromEdgeRc(inputStream, "broken");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testMissingEdgeRc() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("no_such_file");
        ClientCredential.fromEdgeRc(inputStream, "broken");
    }

    @Test(expectedExceptions=NullPointerException.class, dataProvider = "badSections")
    public void testUnparseableSections(String sectionName) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential.fromEdgeRc(inputStream, null);
    }

    @DataProvider
    public Object[][] badSections() {
        return new Object[][] {
                {"bad1"},
                {"bad2"},
                {"bad3"},
                {"bad4" },
        };
    }
}
