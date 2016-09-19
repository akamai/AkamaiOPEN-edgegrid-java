package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EdgeRcClientCredentialProviderTest {

    @Test
    public void testGood1() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good1").getClientCredential(null);
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
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good2").getClientCredential(null);
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
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good3").getClientCredential(null);
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
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good4").getClientCredential(null);
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
        EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "broken").getClientCredential(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testMissingEdgeRc() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("no_such_file");
        EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "broken").getClientCredential(null);
    }

    @Test(expectedExceptions=NullPointerException.class, dataProvider = "badSections")
    public void testUnparseableSections(String sectionName) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, null).getClientCredential(null);
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
