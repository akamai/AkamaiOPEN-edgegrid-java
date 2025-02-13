# EdgeGrid Signer Core

This module is a core implementation, containing base classes and a signing algorithm.

## Overview

The core signing library is designed to be agnostic to any HTTP client library. You can use the core algorithm directly in a non-request context to generate a signature for testing and verification. There are separate libraries for making HTTP requests.

The core library consists of these key classes:

<table>
    <thead>
      <tr>
        <th>Class</th>
        <th>Description</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><code>ClientCredential</code></td>
        <td>
            <p>An immutable container to hold the credential data used for signing requests. You can build one with its internal builder.</p>
<pre lang="java">
  ClientCredential credential = ClientCredential.builder()
    .clientSecret("C113nt53KR3TN6N90yVuAgICxIRwsObLi0E67/N8eRN=")
    .host("akab-h05tnam3wl42son7nktnlnnx-kbob3i3v.luna.akamaiapis.net")
    .accessToken("akab-acc35t0k3nodujqunph3w7hzp7-gtm6ij")
    .clientToken("akab-c113ntt0k3n4qtari252bfxxbsl-yvsdj")
      .build();
</pre>
        </td>
      </tr>
      <tr>
        <td><code>Request</code></td>
        <td>
            <p>An immutable container to hold data about an HTTP request. You can build one with its internal builder.</p>
<pre lang="java">
  Request request = Request.builder()
    .method("PUT")
    .url("https://hostname/identity-management/v3/user-profile/basic-info")
    .body("{\"country\":\"USA\",\"firstName\":\"John\",\"preferredLanguage\":\"English\",\"sessionTimeOut\":30,\"timeZone\":\"GMT\"}".getBytes())
    .header("accept", "application/json")
    .header("content-type", "application/json")
    .build();
</pre>
            <blockquote><strong>NOTE:</strong> You only need to include headers in your <code>Request</code> as part of the EdgeGrid request signature. Many APIs don't require any headers to be signed.</blockquote>
        </td>
      </tr>
      <tr>
        <td><code>EdgeGridV1Signer</code></td>
        <td>
            <p>An implementation of the EdgeGrid V1 signing algorithm. You can use <code>EdgeGridV1Signer().getSignature(Request, ClientCredential)</code> to generate the <code>Authorization</code> header for an EdgeGrid request.</p>
<pre lang="java">
  String authHeader = new EdgeGridV1Signer().getSignature(request, credential);
</pre>
        </td>
      </tr>
    </tbody>
  </table>

## Client library bindings

If you don't want or need to use the core signing library directly, you can use one of the library binding implementations.

The core signing library provides additional classes and interfaces to facilitate using `EdgeGridV1Signer` in real HTTP requests.

| Class | Description|
| ---------- | ----------- |
| `AbstractEdgeGridRequestSigner` | An abstract class that provides scaffolding for library-specific signing implementations. Each implementation has a constructor that takes a `ClientCredential` and another that takes a `ClientCredentialProvider`. |
| `ClientCredentialProvider` | An interface to enable you to code your own mechanism for retrieving a `ClientCredential` when a request is signed, based on the request itself.  This means that you can customize how you retrieve the credential, for example, you can inspect the path being requested to select an appropriate credential. |
| `DefaultClientCredentialProvider` | A simple implementation of `ClientCredentialProvider` which always returns the same `ClientCredential`. The constructors for all the `AbstractEdgeGridRequestSigner` implementations create one of these whenever a `ClientCredential` is passed. |