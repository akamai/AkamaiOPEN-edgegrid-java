/*
 * Copyright 2016 Copyright 2016 Akamai Technologies, Inc. All Rights Reserved.
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

package coma.akamai.testing.edgegrid.googlehttp;


import com.akamai.testing.edgegrid.core.ClientCredential;
import com.akamai.testing.edgegrid.core.RequestSigningException;
import com.akamai.testing.edgegrid.googlehttp.GoogleHttpSignInterceptor;
import com.akamai.testing.edgegrid.googlehttp.GoogleHttpSigner;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Example of use of EdgeGrid signer with Google HTTP Client Library.
 *
 * @author mgawinec@akamai.com
 */
public class OpenApiSample {

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .build();

    @Test(enabled = false)
    public void signEachRequest() throws URISyntaxException, IOException, RequestSigningException {
        HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        GoogleHttpSigner googleHttpSigner = new GoogleHttpSigner();
        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        googleHttpSigner.sign(request, credential);
        request.execute();
    }

    @Test(enabled = false)
    public void withInterceptor() throws URISyntaxException, IOException, RequestSigningException {
        HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
        HttpRequestFactory requestFactory = createSigningRequestFactory(HTTP_TRANSPORT);

        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

        request.execute();
    }

    private HttpRequestFactory createSigningRequestFactory(HttpTransport HTTP_TRANSPORT) {
        return HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
                request.setInterceptor(new GoogleHttpSignInterceptor(credential));
            }
        });
    }
}
