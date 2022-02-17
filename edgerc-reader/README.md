# EdgeRC Reader - EdgeGrid Client for Java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgerc-reader/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgerc-reader)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgerc-reader.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgerc-reader)

This library implements [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.
This particular module is a `ClientCredentialProvider` implementation which is capable of reading credentials from an `.edgerc` file.
This project contains installation and usage instructions in the [README.md](../README.md).

## Overview of EdgeRC Files

The format of an EdgeRC file is simply an INI file where each section corresponds to an authentication token. Each section MUST have the following properties:
* access_token
* client_secret
* client_token
* host

In addition to those 4 required properties, an additional property `max-body` may be present. If
absent, the implied default is 131072. Many users have mysteriously inherited a `max-body` value of
8192 in their EdgeRC files. That value is very unlikely to be correct. If you encounter signature
mismatch errors with POST requests, try removing that value from the file before trying anything
else.

## Using `EdgeRcClientCredentialProvider`

```java
ClientCredential credential = EdgeRcClientCredentialProvider.fromEdgeRc("~/.edgerc", "good1").getClientCredential("section");
```
