# EdgeGrid Client for Java

This library implements [EdgeGrid authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.

Before you begin, you need to [Create authentication credentials](https://techdocs.akamai.com/developer/docs/set-up-authentication-credentials).

## Install required software

In order to use EdgeGrid Client for Java, you need [Java version 8+](https://www.java.com/en/download/help/download_options.xml).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-parent)
[![Javadocs](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-parent.svg)](https://www.javadoc.io/doc/com.akamai.edgegrid)

## Make an API call
You'll need the values for the tokens from your [.edgerc](https://techdocs.akamai.com/developer/docs/set-up-authentication-credentials#add-credential-to-edgerc-file) file.

```
ClientCredential credential = ClientCredential.builder()
        .accessToken("akaa-xxxxxxxxxxxxxxxx-xxxxxxxxxxxxxxxx")
        .clientToken("akaa-xxxxxxxxxxxxxxxx-xxxxxxxxxxxxxxxx")
        .clientSecret("SOMESECRET")
        .host("akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
        .build();
```

Example API call:
```
Request request = Request.builder()
        .method("POST")
        .uri("https://akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net/diagnostic-tools/v2/ghost-locations/available")
        .body("{ \"field\": \"field value\" }".getBytes())
        .header("X-Some-Signed-Header", "header value")
        .header("X-Some-Other-Signed-Header", "header value 2")
        .build();
```

This is an example of an API call to [List available edge server locations](https://techdocs.akamai.com/diagnostic-tools/reference/ghost-locationsavailable). Change the `uri` element to reference an endpoint in any of the [Akamai APIs](https://developer.akamai.com/api).

## Modules

This project contains a core implementation module and five bindings to specific HTTP client libraries.

* [edgegrid-signer-core](edgegrid-signer-core) is the core signing implementation and base classes used by the individual library implementations.
* [edgerc-reader](edgerc-reader) is a configuration file reader that supports `.edgerc` files. These files are basically INI files with certain sections and properties.
* [edgegrid-signer-apache-http-client](edgegrid-signer-apache-http-client) is a binding for [Apache HTTP Client][2].
* [edgegrid-signer-google-http-client](edgegrid-signer-google-http-client) is a binding for [Google HTTP Client Library for Java][3].
* [edgegrid-signer-rest-assured](edgegrid-signer-rest-assured) is a binding for [REST-assured][4].
* [edgegrid-signer-async-http-client](edgegrid-signer-async-http-client) is a binding for [Async HTTP Client][13].


> Note: A number of similar libraries for signing requests exist for popular
programming languages, and you can find them at [https://github.com/akamai?q=edgegrid](https://github.com/akamai?q=edgegrid)


[1]: https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid
[2]: https://hc.apache.org/
[3]: https://github.com/google/google-http-java-client
[4]: https://github.com/rest-assured/rest-assured
[5]: https://github.com/akamai-open/edgegrid-curl
[6]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-python
[7]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-ruby
[8]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-perl
[9]: https://github.com/akamai-open/AkamaiOPEN-powershell
[10]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-node
[11]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-C-Sharp
[12]: https://github.com/akamai-open/AkamaiOPEN-edgegrid-golang
[13]: https://github.com/AsyncHttpClient/async-http-client

## Authors

### Active

Roberto López López <rlopezlo@akamai.com>
Michał Wójcik <miwojci@akamai.com>

### Inactive

Martin Meyer <mmeyer@akamai.com>
Maciej Gawinecki <mgawinec@akamai.com>

## Contribute

This is an open-source library, and contributions are welcome. You're welcome
to fork this project and send us a pull request.

Find valuable resources on the [Akamai Developer](https://developer.akamai.com/) website.
