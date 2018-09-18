# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-apache-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-apache-http-client)

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][1] for Java.
This particular module is a binding for the [Apache HTTP Client library][2].

## Usage of Apache HTTP Client

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-apache-http-client</artifactId>
    <version>3.0.1</version>
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

## Usage with REST-assured

This signing module can also be used with [REST-assured][3] instead of using the
[REST-assured signing module][4]. In this situation you would be configuring the
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

[1]: https://developer.akamai.com/introduction/Client_Auth.html
[2]: https://hc.apache.org/
[3]: https://github.com/rest-assured/rest-assured
[4]: ../edgegrid-signer-rest-assured
