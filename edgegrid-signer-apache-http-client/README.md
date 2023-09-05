# Apache HTTP Client module - EdgeGrid Client for Java

-[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client)
-[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-apache-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-apache-http-client)

This library implements [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.
This particular module is a binding for the [Apache HTTP Client library](https://hc.apache.org/) versions before 5.0.0.
For Apache HTTP Client >= 5.0.0, use `edgegrid-signer-apache-http-client5` module.
This project contains installation and usage instructions in the [README.md](../README.md).

## Use Apache HTTP Client

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-apache-http-client</artifactId>
    <version>5.0.0</version>
</dependency>
```

Create an HTTP client that will sign your HTTP request with a defined client credential:

```java
HttpClient client = HttpClientBuilder.create()
        .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(clientCredential))
        .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(clientCredential))
        .build();

HttpGet request = new HttpGet("http://endpoint.net/billing-usage/v1/reportSources");
client.execute(request);
```

## Use with REST-assured

This signing module can also be used with [REST-assured](https://github.com/rest-assured/rest-assured) instead of using the
[EdgeGrid REST-assured signing module](../edgegrid-signer-rest-assured). In this situation you'd configure the
Apache HTTP Client as the low-level transport for REST-assured requests and the
`RestAssuredEdgeGridFilter` would not be used at all. This may be advantageous
because of some capabilities available to the Apache HTTP Client that are not
available to REST-assured request filters or interceptors. In particular,
REST-assured does not support re-signing requests when it follows a redirect.

To use this module with REST-assured, you need to define an `HttpClientFactory`:

```java
public HttpClientFactory getSigningHttpClientFactory() {
    return new HttpClientConfig.HttpClientFactory() {
            @Override
            public HttpClient createHttpClient() {
                final DefaultHttpClient client = new DefaultHttpClient();
                client.addRequestInterceptor(new ApacheHttpClientEdgeGridInterceptor(clientCredential));
                client.setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(clientCredential));
                return client;
            }
        };
}
```

Next, make `REST-assured` use it:

```java
given()
    .config()
        .httpClient(HttpClientConfig.httpClientConfig().httpClientFactory(getSigningHttpClientFactory()))
.when()
    .get("/billing-usage/v1/reportSources")
.then()
    .statusCode(200);
```
