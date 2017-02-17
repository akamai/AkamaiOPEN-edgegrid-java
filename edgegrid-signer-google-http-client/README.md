# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

[![Build Status](https://travis-ci.org/akamai-open/AkamaiOPEN-edgegrid-java.svg?branch=master)](https://travis-ci.org/akamai-open/AkamaiOPEN-edgegrid-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-google-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-google-http-client)
[![Reference Status](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-google-http-client/reference_badge.svg?style=flat-square)](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-google-http-client/references)
[![Dependency Status](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-google-http-client/badge?style=flat-square)](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-google-http-client)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.akamai.edgegrid/edgegrid-signer-google-http-client/badge.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-google-http-client)

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][11] for Java.
This particular module is a binding for the [Google HTTP Client Library for Java][2].

## Usage with Google HTTP Client Library for Java

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-google-http-client</artifactId>
    <version>2.1.0</version>
</dependency>
```

Sign your HTTP request with a defined client credential:

```java
HttpTransport httpTransport = new ApacheHttpTransport();
HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
URI uri = URI.create("https://akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

GoogleHttpClientEdgeGridRequestSigner requestSigner = new GoogleHttpClientEdgeGridRequestSigner(clientCredential);
requestSigner.sign(request);
request.execute();
```

This, however, requires remembering to sign explicitly every request.
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

And then

```java
HttpRequestFactory requestFactory = createSigningRequestFactory();
URI uri = URI.create("https://akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

request.execute();
```

NOTE: In this example we have used a `ClientCredentialProvider` rather than
a more simple `ClientCredential`. `ClientCredentialProvider` provides a
mechanism to construct a `ClientCredential` at the time of a request based on
any logic you may want. For example, your own implementation could read
credentials from a database or other secret store.

## Changes

2.1:
- Splitting README.md between relevant modules.

2.0:

- Separated binding for Google HTTP Client Library for Java from core
- Unit tests with TestNG

## Authors

Maciej Gawinecki <mgawinec@akamai.com>

Martin Meyer <mmeyer@akamai.com>

## Contribute!

This is an open-source library, and contributions are welcome. You're welcome
to fork this project and send us a pull request.

For more information about OPEN API visit the [Akamai {OPEN} Developer Community](https://developer.akamai.com/).

[1]: https://developer.akamai.com/introduction/Client_Auth.html
[2]: https://github.com/google/google-http-java-client
