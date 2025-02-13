# Apache HTTP Client binding

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-apache-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-apache-http-client)

This module is a binding for the [Apache HTTP Client library before version 5.0.0](https://hc.apache.org/httpcomponents-client-4.5.x/).

For Apache HTTP Client >= 5.0.0, use the [`edgegrid-signer-apache-http-client5`](/edgegrid-signer-apache-http-client5/README.md) module.

## Use

1. Include the Maven dependencies in your project's POM.

    ```xml
    <dependencies>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.14</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgerc-reader</artifactId>
            <version>6.0.1</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgegrid-signer-apache-http-client</artifactId>
            <version>6.0.1</version>
        </dependency>
    </dependencies>
    ```

2. Create an HTTP client that will sign your HTTP request with a defined set of client credentials from a given section, for example, `default`, of your `.edgerc` file.

   ```java
   import java.io.IOException;

   import org.apache.commons.configuration2.ex.ConfigurationException;
   import org.apache.http.client.methods.HttpGet;
   import org.apache.http.impl.client.BasicResponseHandler;
   import org.apache.http.impl.client.CloseableHttpClient;
   import org.apache.http.impl.client.HttpClientBuilder;

   import com.akamai.edgegrid.signer.ClientCredential;
   import com.akamai.edgegrid.signer.EdgeRcClientCredentialProvider;
   import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
   import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;

   public class GetUserProfile {
       public static void main(String[] args) throws ConfigurationException, IOException {
           ClientCredential credential = EdgeRcClientCredentialProvider
                   .fromEdgeRc("~/.edgerc", "default")
                   .getClientCredential(null);

           try (CloseableHttpClient client = HttpClientBuilder.create()
                   .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
                   .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
                   .build()) {

               String uri = "https://" + credential.getHost() + "/identity-management/v3/user-profile";
               System.out.println(client.execute(new HttpGet(uri), new BasicResponseHandler()));
           }
       }
   }
   ```