# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

[![Build Status](https://travis-ci.org/akamai-open/AkamaiOPEN-edgegrid-java.svg?branch=master)](https://travis-ci.org/akamai-open/AkamaiOPEN-edgegrid-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-parent)
[![Reference Status](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-parent/reference_badge.svg?style=flat-square)](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-parent/references)
[![Dependency Status](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-parent/badge?style=flat-square)](https://www.versioneye.com/java/com.akamai.edgegrid:edgegrid-signer-parent)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.akamai.edgegrid/edgegrid-signer-parent/badge.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-parent)

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][1] for Java.
It is presented as a core module which can be used independently of any
particular HTTP client library and three implementations for specific HTTP client
libraries.

## Modules

This project contains a core implementation module and three bindings to specific HTTP client libraries.

* [edgegrid-signer-core](edgegrid-signer-core) is the core signing implementation and base classes used by the individual library implementations.
* [edgegrid-signer-apache-http-client](edgegrid-signer-apache-http-client) is a binding for [Apache HTTP Client][2]
* [edgegrid-signer-google-http-client](edgegrid-signer-google-http-client) is a binding for [Google HTTP Client Library for Java][3]
* [edgegrid-signer-rest-assured](edgegrid-signer-rest-assured) is a binding for [REST-assured][4].

## Changes

2.1:
- Adding binding for Apache HTTP Client.
- Splitting README.md between relevant modules.

2.0:
- Signing algorithm tweaks
- Separated binding for Google HTTP Client Library for Java from core
- Added binding for REST-assured
- Unit tests with TestNG
- Published to Maven Central!

## Similar tools

A number of similar libraries for signing requests exist for popular
programming languages:

* There are two Python bindings: a [command line tool similar to curl][5] and a [Python library][6].
* [Ruby binding][7]
* [Perl binding][8]
* [Powershell binding][9]
* [NodeJS binding][10]
* [C# binding][11]
* [Go binding][12]

[1]: https://developer.akamai.com/introduction/Client_Auth.html
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

## Authors

Maciej Gawinecki <mgawinec@akamai.com>

Martin Meyer <mmeyer@akamai.com>

## Contribute!

This is an open-source library, and contributions are welcome. You're welcome
to fork this project and send us a pull request.

For more information about OPEN API visit the [Akamai {OPEN} Developer Community](https://developer.akamai.com/).
