# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-rest-assured/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-rest-assured)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-rest-assured.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-rest-assured)

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][1] for Java.
This particular module is a binding for the [REST-assured library][2].

## Usage with REST-assured

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-rest-assured</artifactId>
    <version>4.0.1</version>
</dependency>
```

Sign your REST-assured request specification with a defined client credential:

```java
given()
    .filter(new RestAssuredEdgeGridFilter(clientCredential))
.when()
    .get("/billing-usage/v1/reportSources")
.then()
    .statusCode(200);
```

REST-assured request specifications *must* contain a relative path in `get(path)`, `post
(path)` etc.


## Alternative: Use the Apache HTTP Client bindings

REST-assured does not expose certain capabilities effectively. In particular it
does not support re-triggering its filters when following a 301/302 redirect.
The result of following a redirect is presently an invalid request signature and
a rejected request.

If you experience this problem you can use the [Apache HTTP Client binding][3]
instead of the REST-assured binding. This will be functionally equivalent, and
will also sign redirected requests properly. Usage instructions can be found in
the readme for that module.

[1]: https://developer.akamai.com/introduction/Client_Auth.html
[2]: https://github.com/rest-assured/rest-assured
[3]: ../edgegrid-signer-apache-http-client
