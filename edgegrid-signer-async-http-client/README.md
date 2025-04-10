# Async HTTP Client binding

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-async-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-async-http-client)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-async-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-async-http-client)

This module is a binding for the [Async HTTP Client library](https://github.com/AsyncHttpClient/async-http-client).

## Use

1. Include the Maven dependencies in your project's POM.

    ```xml
    <dependencies>
        <dependency>
            <groupId>org.asynchttpclient</groupId>
            <artifactId>async-http-client</artifactId>
            <version>3.0.2</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgerc-reader</artifactId>
            <version>6.0.2</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgegrid-signer-async-http-client</artifactId>
            <version>6.0.2</version>
        </dependency>
    </dependencies>
    ```

2. Create an HTTP client that will sign your HTTP request with a defined set of client credentials from a given section, for example, `default`, of your `.edgerc` file.

    ```java
    import static org.asynchttpclient.Dsl.asyncHttpClient;

    import java.io.IOException;
    import java.util.concurrent.ExecutionException;

    import org.apache.commons.configuration2.ex.ConfigurationException;
    import org.asynchttpclient.AsyncHttpClient;

    import com.akamai.edgegrid.signer.ClientCredential;
    import com.akamai.edgegrid.signer.EdgeRcClientCredentialProvider;
    import com.akamai.edgegrid.signer.ahc.AsyncHttpClientEdgeGridSignatureCalculator;

    public class GetUserProfile {
        public static void main(String[] args) throws ConfigurationException, IOException, ExecutionException, InterruptedException {
            ClientCredential credential = EdgeRcClientCredentialProvider
                    .fromEdgeRc("~/.edgerc", "default")
                    .getClientCredential(null);

            try (AsyncHttpClient client = asyncHttpClient()
                    .setSignatureCalculator(new AsyncHttpClientEdgeGridSignatureCalculator(credential))) {
                String uri = "https://" + credential.getHost() + "/identity-management/v3/user-profile";
                System.out.println(client.prepareGet(uri).execute().get());
            }
        }
    }
    ```
