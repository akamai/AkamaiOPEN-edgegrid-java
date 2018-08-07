# EdgeGrid Client for Java

Java implementation of Akamai {OPEN} EdgeGrid signing.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-gatling/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-gatling)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-gatling.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-gatling)

## Description

This library implements [Akamai {OPEN} EdgeGrid Authentication][1] for Java.
This particular module is a binding for the [Gatling][2].

## Usage of Gatling

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-gatling</artifactId>
    <version>3.0.0</version>
</dependency>
```

Create a Gatling simulation that inherits from `OpenApiSimulation` and is constructed with a defined
client credential:

```scala
class YourSimulation extends OpenApiSimulation(clientCredential) {

  ...

 val testScenario = scenario("Test scenario")
    .exec(
      http("createPropertyRequest")
        .post("/papi/v0/properties/")
        .queryParam("contractId", "ctr_1-3CV382")
        .queryParam("groupId", "grp_18385")
        .body(StringBody(
          """{
                "productId": "Site_Accel",
                "propertyName": "8LuWyUjwea"
             }"""))
        .asJSON
        .check(status.is(201))
    )

  setUp(
    testScenario.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
```

Note, `httpConf` is a protocol configured to use the defined `clientCredential`.

The binding works only with Gatling 2. It will not work Gatling 3.





[1]: https://developer.akamai.com/introduction/Client_Auth.html
[2]: https://gatling.io/
