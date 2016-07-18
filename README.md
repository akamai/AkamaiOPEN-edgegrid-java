# EdgeGrid V1 Signer binding for REST-assured

Signs [REST-assured][11] requests to OPEN API services, using EdgeGrid V1 signing algorithm.

## Usage

Include the following Maven dependency in your project POM:

```xml
<dependency>
  <groupId>com.akamai.testing</groupId>
  <artifactId>edgegrid-v1-signer-restassured</artifactId>
  <version>1.0</version>
</dependency>
```

Define client credential

```java
ClientCredential credential = ClientCredential.builder()
  .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz2rp")
  .clientSecret("12rvdn/myhSSiuYAC6ZPGaI91ezhdbYd7WyagzhGxms=")
  .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
  .build();
```      

that you will use to sign your REST-assured request:

```java
given().
  baseUri("https://akaa-cf6fooumkselbx6j-spubmyp7ygje4vyx.luna-dev.akamaiapis.net").
  filter(EdgeGridV1SignerFilter.sign(credential)).
when().
  get("/authz/v2/identities/self").
then().
  statusCode(200);
```

## Vision

The idea behind the tool is to create a Java binding for signing OPEN API requests that is agnostic to the type of 
HTTP client used. The core module is *edgegrid-v1-signer-core*, on top of which a specific binding can be built, e.g., 
*edgegrid-v1-signer-restassured*.

## Releases 

1.0

## Bugs and features request

Report or request in ??

## Similar tools

A number of similar libraries for signing requests exist for popular programming languages:

* There are two Python bindings: a [command line tool similar to curl][1] and a [Python library][2].
* [Ruby binding][2]
* [Perl binding][3]
* [Powershell binding][4]
* [NodeJS binding][5]
* [C# binding][6]
* [Go binding][7]
* [Java binding][9] built on the top of [Google HTTP Client][10].

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
