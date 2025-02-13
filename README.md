# EdgeGrid Client for Java

This library implements an Authentication handler for the [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) scheme in Java.

## Install

To use AkamaiOPEN EdgeGrid for Java, you need Java version 11+.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-parent)
[![Javadocs](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-parent.svg)](https://www.javadoc.io/doc/com.akamai.edgegrid)

## Modules

This project contains core implementation modules and binding modules to specific HTTP client libraries.

### Core

| Module | Description |
| ---------- | ------------ |
| [edgegrid-signer-core](edgegrid-signer-core) | The core signing implementation and base classes used by an individual library. |
| [edgegrid-reader](edgegrid-reader) | A configuration file reader that reads credentials from an `.edgerc` file. This file is an INI file with credential sections and properties. |

See the [Authentication](#authentication) section for details on using the classes from these modules.

### Bindings

| Module | Description |
| ---------- | ------------ |
| [edgegrid-signer-apache-http-client](edgegrid-signer-apache-http-client) | A binding for [Apache HTTP Client before version 5.0.0](https://hc.apache.org/httpcomponents-client-4.5.x/). |
| [edgegrid-signer-apache-http-client5](edgegrid-signer-apache-http-client5) | A binding for [Apache HTTP Client version 5.x](https://hc.apache.org/httpcomponents-client-5.4.x/). |
| [edgegrid-signer-async-http-client](edgegrid-signer-async-http-client) | A binding for [Async HTTP Client](https://github.com/AsyncHttpClient/async-http-client). |
| [edgegrid-signer-google-http-client](edgegrid-signer-google-http-client) | A binding for [Google HTTP Client](https://github.com/google/google-http-java-client). |
| [edgegrid-signer-rest-assured](edgegrid-signer-rest-assured) | A binding for [REST-assured](https://github.com/rest-assured/rest-assured). |


> __Note__: Several similar libraries for signing requests exist for popular
programming languages, and you can find them at [https://github.com/akamai?q=edgegrid](https://github.com/akamai?q=edgegrid).

## Authentication

You can get the authentication credentials through an API client. Requests to the API are marked with a timestamp and a signature and are executed immediately.

1. [Create authentication credentials](https://techdocs.akamai.com/developer/docs/set-up-authentication-credentials).

2. Place your credentials in an EdgeGrid resource file `~/.edgerc`, in the `[default]` section.

    ```
    [default]
    client_secret = C113nt53KR3TN6N90yVuAgICxIRwsObLi0E67/N8eRN=
    host = akab-h05tnam3wl42son7nktnlnnx-kbob3i3v.luna.akamaiapis.net
    access_token = akab-acc35t0k3nodujqunph3w7hzp7-gtm6ij
    client_token = akab-c113ntt0k3n4qtari252bfxxbsl-yvsdj
    ```

    In addition to the required properties, an `.edgerc` file can optionally contain a `max-body` property. If absent, the implied default is 131072.

    If you've inherited a `max-body` value of 8192 in your `.edgerc` file, that value is incorrect. If you encounter signature mismatch errors with POST requests, try removing that value from the file before trying anything else.

3. Use your local `.edgerc` by providing the path to your resource file and credentials' section header in the  `EdgeRcClientCredentialProvider` class from the `edgerc-reader` module.

    ```java
    ClientCredential credential = EdgeRcClientCredentialProvider
            .fromEdgeRc("path/to/.edgerc", "your-section-header")
            .getClientCredential(null);
    ```

    Or hard code your credentials and pass the values to the `ClientCredential` class from the `edgegrid-signer-core` module.

    ```java
    ClientCredential credential = ClientCredential.builder()
            .clientSecret("C113nt53KR3TN6N90yVuAgICxIRwsObLi0E67/N8eRN=")
            .host("akab-h05tnam3wl42son7nktnlnnx-kbob3i3v.luna.akamaiapis.net")
            .accessToken("akab-acc35t0k3nodujqunph3w7hzp7-gtm6ij")
            .clientToken("akab-c113ntt0k3n4qtari252bfxxbsl-yvsdj")
            .build();
    ```

## Use

Using one of the bindings to the HTTP client libraries, provide the path to your `.edgerc`, your credentials' section header, and the appropriate endpoint information.

The following is an example of making an HTTP call with Apache HTTP Client 5, one of the HTTP client libraries for Java that our EdgeGrid plug-in supports.

```java
import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.EdgeRcClientCredentialProvider;
import com.akamai.edgegrid.signer.apachehttpclient5.ApacheHttpClient5EdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient5.ApacheHttpClient5EdgeGridRoutePlanner;

public class GetUserProfile {
    public static void main(String[] args) throws ConfigurationException, IOException {
        ClientCredential credential = EdgeRcClientCredentialProvider
                .fromEdgeRc("~/.edgerc", "default")
                .getClientCredential(null);

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .addRequestInterceptorFirst(new ApacheHttpClient5EdgeGridInterceptor(credential))
                .setRoutePlanner(new ApacheHttpClient5EdgeGridRoutePlanner(credential))
                .build()) {

            String uri = "https://" + credential.getHost() + "/identity-management/v3/user-profile";
            System.out.println(client.execute(new HttpGet(uri), new BasicHttpClientResponseHandler()));
        }
    }
}
```

For details on how to make a call using this and other bindings to the HTTP client libraries, see each binding module's `README.md` file.

## Reporting issues

To report an issue or make a suggestion, create a new [GitHub issue](https://github.com/akamai/AkamaiOPEN-edgegrid-java/issues).

## License

Copyright 2025 Akamai Technologies, Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use these files except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.