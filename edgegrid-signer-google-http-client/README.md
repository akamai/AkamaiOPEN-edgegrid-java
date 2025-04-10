# Google HTTP Client binding

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-google-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-google-http-client)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-google-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-google-http-client)

This module is a binding for the [Google HTTP Client library](https://github.com/google/google-http-java-client).

## Use

1. Include the Maven dependencies in your project's POM.

    ```xml
    <dependencies>
        <dependency>
            <groupId>com.google.http-client</groupId>
            <artifactId>google-http-client</artifactId>
            <version>1.45.3</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgerc-reader</artifactId>
            <version>6.0.2</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgegrid-signer-google-http-client</artifactId>
            <version>6.0.2</version>
        </dependency>
    </dependencies>
    ```

2. Create an HTTP client that will sign your HTTP request with a defined set of client credentials from a given section, for example, `default`, of your `.edgerc` file.

    ```java
    import java.io.IOException;

    import org.apache.commons.configuration2.ex.ConfigurationException;

    import com.akamai.edgegrid.signer.ClientCredential;
    import com.akamai.edgegrid.signer.EdgeRcClientCredentialProvider;
    import com.akamai.edgegrid.signer.googlehttpclient.GoogleHttpClientEdgeGridInterceptor;
    import com.google.api.client.http.GenericUrl;
    import com.google.api.client.http.HttpRequest;
    import com.google.api.client.http.HttpRequestFactory;
    import com.google.api.client.http.javanet.NetHttpTransport;

    public class GetUserProfile {

        public static void main(String[] args) throws ConfigurationException, IOException {
            ClientCredential credential = EdgeRcClientCredentialProvider
                    .fromEdgeRc("~/.edgerc", "default")
                    .getClientCredential(null);

            HttpRequestFactory factory = new NetHttpTransport().createRequestFactory(request ->
                    request.setInterceptor(new GoogleHttpClientEdgeGridInterceptor(credential)));

            String uri = "https://" + credential.getHost() + "/identity-management/v3/user-profile";
            HttpRequest request = factory.buildGetRequest(new GenericUrl(uri));
            System.out.println(request.execute().parseAsString());
        }
    }
    ```

    > **Note:** You can use `GoogleHttpClientEdgeGridRequestSigner` directly instead of using it through `GoogleHttpClientEdgeGridInterceptor`. This approach, however, will result in a more verbose code.