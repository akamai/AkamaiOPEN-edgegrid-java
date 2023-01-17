# Async HTTP Client library - EdgeGrid Client for Java

-[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-async-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-async-http-client)
-[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-async-http-client.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-async-http-client)

This library implements [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.
This particular module is a binding for the [Async HTTP Client library](https://github.com/AsyncHttpClient/async-http-client).
This project contains installation and usage instructions in the [README.md](../README.md).

## Use Async HTTP Client

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-async-http-client</artifactId>
    <version>5.0.0</version>
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

> Note: The `host` part of the URI will be replaced by the provided `AsyncHttpClientEdgeGridSignatureCalculator`  
with the host from the client credential in the `.edgerc` file.

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

> Note: In this case you need to prepare requests with the HTTP client.
