# Apache HTTP Client 5 module - EdgeGrid Client for Java

-[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client5/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-apache-http-client5)
-[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-apache-http-client5.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-apache-http-client5)

This library implements [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.
This particular module is a binding for the [Apache HTTP Client library version 5.x](https://hc.apache.org/).
This project contains installation and usage instructions in the [README.md](../README.md).

## Use Apache HTTP Client

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-apache-http-client5</artifactId>
    <version>${version}</version>
</dependency>
```

Or in Gradle's `build.gradle.kts`
```kotlin
implementation("com.akamai.edgegrid:edgegrid-signer-apache-http-client5:$version")
```

Create an HTTP client that will sign your HTTP request with a defined client credential:

```java
var client=HttpClientBuilder.create()
        .addRequestInterceptorFirst(new ApacheHttpClient5EdgeGridInterceptor(credential))
        .setRoutePlanner(new ApacheHttpClient5EdgeGridRoutePlanner(credential))
        .build();

        var request=new HttpGet("http://endpoint.net/billing-usage/v1/reportSources");
        client.execute(request,response->{
            // response handler
        });
```

## Use with REST-assured

[REST-assured](https://github.com/rest-assured/rest-assured) doesn't currently support Apache HTTP Client 5. Refer to
this [README](/edgegrid-signer-apache-http-client/README.md) in `edgegrid-signer-apache-http-client` module to set up
an interceptor for a legacy client.
