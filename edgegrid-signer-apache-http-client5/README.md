# Apache HTTP Client 5 binding

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client5/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client5)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-apache-http-client5.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-apache-http-client5)

This module is a binding for the [Apache HTTP Client library version 5.x](https://hc.apache.org/httpcomponents-client-5.4.x/).

## Use

1. Include the Maven dependencies in your project's POM.

    ```xml
    <dependencies>
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.4.1</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgerc-reader</artifactId>
            <version>6.0.2</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgegrid-signer-apache-http-client5</artifactId>
            <version>6.0.2</version>
        </dependency>
    </dependencies>
    ```

2. Create an HTTP client that will sign your HTTP request with a defined set of client credentials from a given section, for example, `default`, of your `.edgerc` file.

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