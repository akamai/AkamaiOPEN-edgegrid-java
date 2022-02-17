# Gatling - EdgeGrid Client for Java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-gatling/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-gatling)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-gatling.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-gatling)

This library implements [Akamai EdgeGrid Authentication](https://techdocs.akamai.com/developer/docs/authenticate-with-edgegrid) for Java.
This particular module is a binding for [Gatling](https://gatling.io/).
This project contains installation and usage instructions in the [README.md](../README.md).

## Use Gatling

> Note: The binding works only with Gatling 2, not Gatling 3.

Include the following Maven dependency in your project POM:

```xml
<dependency>
    <groupId>com.akamai.edgegrid</groupId>
    <artifactId>edgegrid-signer-gatling</artifactId>
    <version>4.1.1</version>
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

> Note: `httpConf` is a protocol configured to use the defined `clientCredential`.
