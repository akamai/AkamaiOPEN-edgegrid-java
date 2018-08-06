# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

[![Build Status](https://travis-ci.org/akamai-open/AkamaiOPEN-edgegrid-java.svg?branch=master)](https://travis-ci.org/akamai-open/AkamaiOPEN-edgegrid-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-async-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-async-http-client)
[![Reference Status](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-async-http-client/reference_badge.svg?style=flat-square)](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-async-http-client/references)
[![Dependency Status](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-async-http-client/badge?style=flat-square)](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-async-http-client)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.akamai.edgegrid/edgegrid-signer-async-http-client/badge.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-async-http-client)

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][1] for Java.
This particular module is a binding for the [Async HTTP Client library][2].

## Usage of Async HTTP Client

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-async-http-client</artifactId>
    <version>3.0.0</version>
</dependency>
```

Create an HTTP request that will be signed with a defined client credential:

```java
Request request = new RequestBuilder("POST")
    .setUrl("https://localhost/papi/v0/properties")
    .addQueryParam("contractId","ctr_1-3CV382")
    .addQueryParam("groupId","grp_18385")
    .setBody("{ \"productId\": \"Site_Accel\", \"propertyName\": \"8LuWyUjwea\" }")
    .setHeader("Content-Type", "application/json")
    .setSignatureCalculator(new AsyncHttpClientEdgeGridSignatureCalculator(clientCredential))
    .build();

asyncHttpClient().executeRequest(request).get();
```

Alternatively, create an HTTP client that will sign each HTTP request with a defined client 
credential:

```java
AsyncHttpClient client = asyncHttpClient()
    .setSignatureCalculator(new AsyncHttpClientEdgeGridSignatureCalculator(clientCredential));

client.preparePost("https://localhost/papi/v0/properties")
    .addQueryParam("contractId","ctr_1-3CV382")
    .addQueryParam("groupId","grp_18385")
    .setBody("{ \"productId\": \"Site_Accel\", \"propertyName\": \"8LuWyUjwea\" }")
    .setHeader("Content-Type", "application/json")
    .execute().get();
```

Note, in the latter case requests *must* be prepared with the HTTP client.

Note, in both cases the host part of the URI does not matter, because it will be replaced by
the provided `AsyncHttpClientEdgeGridSignatureCalculator`  with the host from the provided client 
credential.

[1]: https://developer.akamai.com/introduction/Client_Auth.html
[2]: https://github.com/AsyncHttpClient/async-http-client
