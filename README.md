# EdgeGrid for Java

Java implementation of Akamai {OPEN} EdgeGrid signing in Java.

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][11] for Java.
It is presented as a core module which can be used independently of any
particular HTTP client library and two implementations for specific HTTP client
libraries.

## Overview of core library

The core library is designed to be agnostic to any particular HTTP Client
library. The core algorithm can be used directly in a non-request context to
generate a signature for testing and verification. There are separate libraries
for actually making HTTP requests - see below.

The EdgeGrid Signer Core library consists of three key classes.

`ClientCredential` is an immutable container to hold the credential data used
for signing requests. You can build one with its internal builder:

```java
ClientCredential credential = ClientCredential.builder()
        .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
        .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
        .clientSecret("SOMESECRET")
        .host("akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
        .build();
```

`Request` is an immutable container to hold data about an HTTP request. You can
build one with its internal builder:

```java
Request request = Request.builder()
        .method("POST")
        .uriWithQuery(URI.create("https://localhost/service/v2/users"))
        .body("{ field: \"foo\" }")
        .header("content-type", "application/json")
        .build();
```

`EdgeGridV1Signer` is an implementation of the EdgeGrid V1 Signing Algorithm.
You can use `EdgeGridV1Signer#getSignature(Request, ClientCredential)` to
generate the `Authorization` header for an EdgeGrid request:

```java
String authHeader = new EdgeGridV1Signer().getSignature(request, credential);
```

## Client Library Bindings

The core signing library is agnostic to any particular HTTP client. Most users
will not want or need to use it directly, they will want to use one of the
library binding implementations.

The core signing library provides some additional classes and interfaces to
facilitate usage of `EdgeGridV1Signer` in real HTTP requests.

`AbstractEdgeGridRequestSigner` is an abstract class that provides most of the
scaffolding for library-specific signing implementations. Each implementation
has a constructor that takes a `ClientCredential` and another that takes a
`ClientCredentialProvider`.

`ClientCredentialProvider` is an interface to permit the user to code their
own mechanism for retrieving a `ClientCredential` at the time a request is
signed based on the request itself. This means that implementations can, for
example, inspect the path being requested in order to select an appropriate
credential.

`DefaultClientCredentialProvider` is a simple implementation of
`ClientCredentialProvider` which always returns the same `ClientCredential`.
The constructors for all the `AbstractEdgeGridRequestSigner` implementations
create one of these transparently whenever they are passed a `ClientCredential`.

`EdgeRcClientCredentialProvider` is another implementation of
`ClientCredentialProvider` that can read from the EdgeRc configuration files
 that are used in various other EdgeGrid signing library implementations. The
`#pickSectionName()` method can be overridden by the user to select different
sections from the configuration file based on the current request.


## Usage with REST-assured

There is an EdgeGrid signer implementation for [REST-assured][10].

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-rest-assured</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>
```

Sign your REST-assured request specification with a defined client credential:

```java
given()
    .baseUri("https://endpoint.net")
    .filter(new RestAssuredEdgeGridFilter(credential))
.when()
    .get("/service/v2/users")
.then()
    .statusCode(200);
```

REST-assured request specifications *must* contain a relative path in `get(path)`, `post
(path)` etc. 

## Usage with Google HTTP Client Library for Java

There is an EdgeGrid signer implementation for [Google HTTP Client Library for Java][9].

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-google-http-client</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>
```

Sign your HTTP request with a defined client credential:

```java
HttpTransport httpTransport = new ApacheHttpTransport();
HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

GoogleHttpClientEdgeGridRequestSigner requestSigner = new GoogleHttpClientEdgeGridRequestSigner();
requestSigner.sign(request, credential);
request.execute();
```

This, however, requires remembering to sign explicitly every request. Alternatively, you may create <code>HttpRequestFactory</code>
that will be doing it for yourself:

```java
private HttpRequestFactory createSigningRequestFactory(HttpTransport httpTransport) {
    return httpTransport.createRequestFactory(new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {
            request.setInterceptor(new GoogleHttpClientEdgeGridInterceptor(credential));
        }
    });
}
```

And then

```java
HttpTransport httpTransport = new ApacheHttpTransport();
HttpRequestFactory requestFactory = createSigningRequestFactory(httpTransport);
URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

request.execute();
```

## Releases

2.0:

- signing algorithm tweaks
- binding for Google HTTP Client Library for Java
- binding for REST-assured
- Unit tests with TestNG
- 2.0 will be published to Maven Central!

## Similar tools

A number of similar libraries for signing requests exist for popular
programming languages:

* There are two Python bindings: a [command line tool similar to curl][1] and a [Python library][2].
* [Ruby binding][2]
* [Perl binding][3]
* [Powershell binding][4]
* [NodeJS binding][5]
* [C# binding][6]
* [Go binding][7]

[1]: https://github.com/akamai-open/edgegrid-curl
[2]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-python
[3]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-ruby
[4]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-perl
[5]: https://github.com/akamai-open/AkamaiOPEN-powershell
[6]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-node
[7]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-C-Sharp
[8]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-golang
[9]: https://github.com/google/google-http-java-client
[10]: https://github.com/rest-assured/rest-assured
[11]: https://developer.akamai.com/introduction/Client_Auth.html
[12]: https://developer.akamai.com/

## Authors

Maciej Gawinecki <mgawinec@akamai.com>

Martin Meyer <mmeyer@akamai.com>


## Contribute!

This is an open-source library, and contributions are welcome. You're welcome
to fork this project and send us a pull request.

For more information about OPEN API visit the [Akamai {OPEN} Developer Community][12].
