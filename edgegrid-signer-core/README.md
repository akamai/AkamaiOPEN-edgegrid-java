# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][1] for Java.
This particular module is the core implementation, containing base classes and
a signing algorithm implementation.

## Overview of core library

The core library is designed to be agnostic to any particular HTTP client
library. The core algorithm can be used directly in a non-request context to
generate a signature for testing and verification. There are separate libraries
for actually making HTTP requests.

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
        .uri("https://akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net/billing-usage/v1/reportSources")
        .body("{ \"field\": \"field value\" }".getBytes())
        .header("X-Some-Signed-Header", "header value")
        .header("X-Some-Other-Signed-Header", "header value 2")
        .build();
```

NOTE: You only need to include headers in your `Request` that will be included
in the EdgeGrid request signature. Many APIs do not require any headers to be
signed.

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

## Changes

2.1:
- Splitting README.md between relevant modules.

2.0:
- Signing algorithm tweaks
- Separated binding for Google HTTP Client Library for Java from core
- Unit tests with TestNG
- Published to Maven Central

## Authors

Maciej Gawinecki <mgawinec@akamai.com>

Martin Meyer <mmeyer@akamai.com>

## Contribute!

This is an open-source library, and contributions are welcome. You're welcome
to fork this project and send us a pull request.

For more information about OPEN API visit the [Akamai {OPEN} Developer Community](https://developer.akamai.com/).

[1]: https://developer.akamai.com/introduction/Client_Auth.html
