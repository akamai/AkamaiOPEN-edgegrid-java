/*
 * Copyright 2018 Akamai Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link EdgeRcClientCredentialProvider}.
 *
 * @author mmeyer@akamai.com
 */
public class EdgeRcClientCredentialProviderTest {

    @Test
    public void testGood1() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good1").getClientCredential("good1");
        assertThat(credential.getAccessToken(), is(equalTo("AT1")));
        assertThat(credential.getClientSecret(), is(equalTo("CS1")));
        assertThat(credential.getClientToken(), is(equalTo("CT1")));
        assertThat(credential.getHost(), is(equalTo("HOST1")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(65536));
    }

    @Test
    public void testGood2() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good2").getClientCredential("good2");
        assertThat(credential.getAccessToken(), is(equalTo("AT2")));
        assertThat(credential.getClientSecret(), is(equalTo("CS2")));
        assertThat(credential.getClientToken(), is(equalTo("CT2")));
        assertThat(credential.getHost(), is(equalTo("HOST2")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test
    public void testGood3() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good3").getClientCredential("good3");
        assertThat(credential.getAccessToken(), is(equalTo("AT3")));
        assertThat(credential.getClientSecret(), is(equalTo("CS3")));
        assertThat(credential.getClientToken(), is(equalTo("CT3")));
        assertThat(credential.getHost(), is(equalTo("HOST3")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test
    public void testGood4() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good4").getClientCredential("good4");
        assertThat(credential.getAccessToken(), is(equalTo("AT4")));
        assertThat(credential.getClientSecret(), is(equalTo("CS4")));
        assertThat(credential.getClientToken(), is(equalTo("CT4")));
        assertThat(credential.getHost(), is(equalTo("HOST4")));
        assertThat(credential.getHeadersToSign(), is(empty()));
        assertThat(credential.getMaxBodySize(), is(131072));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testMalformedEdgeRc() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc_malformed");
        EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "broken").getClientCredential("broken");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testMissingEdgeRc() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("no_such_file");
        EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "broken").getClientCredential("broken");
    }

    @Test(expectedExceptions=IllegalArgumentException.class, dataProvider = "badSections")
    public void testUnparseableSections(String sectionName) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edgerc");
        // NOTE: Specifying a different default section name ensures we honor the method argument.
        EdgeRcClientCredentialProvider.fromEdgeRc(inputStream, "good1").getClientCredential(sectionName);
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
