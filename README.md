# EdgeGrid V1 Signer bindings for Java

## Description

Signs HTTP requests to OPEN API services, using EdgeGrid V1 signing algorithm.

EdgeGrid signing algorithm implementation is agnostic HTTP client. Then you can use or create with a minimal effort 
a binding specific for HTTP client. Currently two bindings are available:

* binding for [REST-assured][11]
* binding for [Google HTTP Client Library for Java][10]

This is an open-source library, and contributions are welcome. 

## Usage

### Usage with REST-assured

Include the following Maven dependency in your project POM:

```xml
<dependency>
  <groupId>com.akamai.testing</groupId>
  <artifactId>edgegrid-v1-signer-restassured</artifactId>
  <version>1.0</version>
</dependency>
```

Define a client credential

```java
ClientCredential credential = ClientCredential.builder()
  .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
  .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
  .clientSecret("SOMESECRET")
  .build();
```      

that you will use to sign your REST-assured request:

```java
given().
  baseUri("https://endpoint.net").
  filter(EdgeGridV1SignerFilter.sign(credential)).
when().
  get("/service/v2/users").
then().
  statusCode(200);
```

### Usage with Google HTTP Client Library for Java

Include the following Maven dependency in your project POM:

```xml
<dependency>
  <groupId>com.akamai.testing</groupId>
  <artifactId>edgegrid-v1-signer-googlehttp</artifactId>
  <version>1.0</version>
</dependency>
```

Define a client credential

```java
ClientCredential credential = ClientCredential.builder()
  .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
  .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
  .clientSecret("SOMESECRET")
  .build();
```      

that you will use to sign your Google HTTP client request:

```java
HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
GoogleHttpSigner googleHttpSigner = new GoogleHttpSigner();
URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
googleHttpSigner.sign(request, credential);
request.execute();
```

This, however, requires remembering to sign expliclty every request. Alternatively, you may create <code>HttpRequestFactory</code>
that will be doing it for yourself:

```java
private HttpRequestFactory createSigningRequestFactory(HttpTransport HTTP_TRANSPORT) {
    return HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {
            request.setInterceptor(new GoogleHttpSignInterceptor(credential));
        }
    });
}
```

And then

```java
HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
HttpRequestFactory requestFactory = createSigningRequestFactory(HTTP_TRANSPORT);
URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
request.execute();
```        

## Releases 

1.0:

- signing algorithm
- binding for Google HTTP Client Library for Java
- binding for REST-assured

## Similar tools

A number of similar libraries for signing requests exist for popular programming languages:

* There are two Python bindings: a [command line tool similar to curl][1] and a [Python library][2].
* [Ruby binding][2]
* [Perl binding][3]
* [Powershell binding][4]
* [NodeJS binding][5]
* [C# binding][6]
* [Go binding][7]
* [Java binding][9] built on the top of [Google HTTP Client Library for Java][10].

[1]: https://github.com/akamai-open/edgegrid-curl
[2]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-python
[3]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-ruby
[4]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-perl
[5]: https://github.com/akamai-open/AkamaiOPEN-powershell
[6]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-node
[7]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-C-Sharp
[8]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-golang
[9]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-java
[10]: https://github.com/google/google-http-java-client
[11]: https://github.com/rest-assured/rest-assured
