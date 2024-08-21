# Change log

## 6.0.0 (August 21, 2024)

### BREAKING CHANGES

* Replaced deprecated `ApacheHttpTransport` with `com.google.api.client.http.apache.v2.ApacheHttpTransport` in `edgegrid-signer-google-http-client`.
* Updated `README.md` for `edgegrid-signer-google-http-client` to include changes in the instructions for signing HTTP requests with specified client credentials.

### Improvements

* Add support for `ProxySelector` in `ApacheHttpClientEdgeGridRoutePlanner` to enable the use of custom proxy servers.

### Fixes

* Fixes for various vulnerabilities by upgrading `grpc-context`, `netty` and `commons-configuration2`.
* Fixed issue when path param is an url for rest assured


## 5.1.1 (December 6, 2023)

### Fixes

* Fixes for various CVE vulnerabilities by upgrading logback classic, netty and dependency-check.


## 5.1.0 (September 5, 2023)

### Improvements

* Add support for Apache HTTP Client version 5.

### Fixes

* Fixes for various CVE vulnerabilities by upgrading netty, dependency-check and guava libraries.
* Fixes some build errors by upgrading Jacoco library.
* Resolve various Javadoc warnings in different modules.

## 5.0.0 (January 19, 2023)

### BREAKING CHANGES

*  Minimum Java version is 11.

## 4.1.2 (July 21, 2022)

### Improvements

* Extract edgegrid-signer-gatling module to a separate project.

### Fixes

* Fixes for various vulnerabilities: OSSRH-66257, CVE-2020-36518, sonatype-2021-4682, CVE-2022-24823, sonatype-2019-0673, sonatype-2012-0050, sonatype-2021-4916

## 4.1.1 (February 17, 2022)

### Enhancements

* Added OWASP dependency check plugin to maven pipeline.

### Fixes

* Fix multiple CVE vulnerabilities by upgrading logback and netty dependencies.
* Fix Travis build by updating Java version to 8.
* Correct README.md inconsistencies.

## 4.1.0 (August 26, 2021)

### Enhancements

* Upgrade project dependencies.
* Ensure compatibility with Java >= v9.

## 4.0.1

### Fixes

* Fix Issue #35, a broken unit test.
* Use [URI#getRawPath()](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html#getRawPath--) when constructing a signature.

## 4.0

### BREAKING CHANGES

* Split the edgerc file reader into new module [edgerc-reader](edgerc-reader).
* Drop dependency on commons-configuration2 from edgegrid-signer-core.
* Drop dependency on commons-lang3.
* Drop dependency on commons-codec (use Base64 methods from JDK instead).
* Use maven-bundle-plugin to add OSGi headers to MANIFEST.MF.

## 3.0

### BREAKING CHANGES

* Minimum Java version is now 8.

### Improvements

* Adding binding for Async HTTP Client.
* Adding binding for Gatling.

## 2.1

### Improvements

* Adding binding for Apache HTTP Client.
* Splitting README.md between relevant modules.

## 2.0

### Improvements

* Signing algorithm tweaks
* Separating binding for Google HTTP Client Library for Java from core
* Adding binding for REST-assured
* Unit tests with TestNG
* Publishing to Maven Central!