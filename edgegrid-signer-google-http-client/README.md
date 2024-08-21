# Google HTTP Client Library - EdgeGrid Client for Java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-google-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-google-http-client)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-google-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-google-http-client)

This library implements [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.
This particular module is a binding for [Google HTTP Client Library for Java](https://github.com/google/google-http-java-client).
This project contains installation and usage instructions in the [README.md](../README.md).

## Use Google HTTP Client Library for Java

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-google-http-client</artifactId>
    <version>5.0.0</version>
</dependency>
```

Sign your HTTP request with a defined client credential:

```java
HttpClient client = HttpClients.custom()
        .setSSLSocketFactory(SSLSocketFactory.getSystemSocketFactory())
        .build();
HttpRequestFactory requestFactory = new ApacheHttpTransport(client).createRequestFactory();
URI uri = URI.create("https://akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net/billing-usage/v1/reportSources");
try {
    HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
    GoogleHttpClientEdgeGridRequestSigner requestSigner = new GoogleHttpClientEdgeGridRequestSigner(credential);

    requestSigner.sign(request);
    request.execute();
} catch (IOException | RequestSigningException e) {
    throw new RuntimeException("Error during HTTP request execution", e);
}
```


This, however, requires remembering to sign every request explicitly.

Alternately, you may create an `HttpRequestFactory` that will automatically
sign requests via an Interceptor:

```java
private HttpRequestFactory createSigningRequestFactory() {
    HttpTransport httpTransport = new ApacheHttpTransport();
    return httpTransport.createRequestFactory(new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {
            request.setInterceptor(new GoogleHttpClientEdgeGridInterceptor(clientCredentialProvider));
        }
    });
}
```

And then:

```java
HttpRequestFactory requestFactory = createSigningRequestFactory();
URI uri = URI.create("https://akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

request.execute();
```

> Note: In this example we used a `clientCredentialProvider` rather than
a simpler `ClientCredential`. `clientCredentialProvider` provides a
mechanism to construct a `ClientCredential` at the time of the request based on
any logic you define. For example, your implementation could read
credentials from a database or another secret store.
