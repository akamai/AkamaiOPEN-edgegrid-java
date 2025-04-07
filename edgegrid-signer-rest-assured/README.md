# REST-assured binding

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-rest-assured/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.akamai.edgegrid/edgegrid-signer-rest-assured)
[![Javadoc](http://www.javadoc.io/badge/com.akamai.edgegrid/edgegrid-signer-rest-assured.svg)](http://www.javadoc.io/doc/com.akamai.edgegrid/edgegrid-signer-rest-assured)

This module is a binding for the [REST-assured library](https://github.com/rest-assured/rest-assured).

## Use

1. Include the Maven dependencies in your project's POM.

    ```xml
    <dependencies>
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>5.5.0</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgerc-reader</artifactId>
            <version>6.0.2</version>
        </dependency>

        <dependency>
            <groupId>com.akamai.edgegrid</groupId>
            <artifactId>edgegrid-signer-rest-assured</artifactId>
            <version>6.0.2</version>
        </dependency>
    </dependencies>
    ```

2. Create an HTTP client that will sign your HTTP request with a defined set of client credentials from a given section, for example, `default`, of your `.edgerc` file.

    ```java
    import static io.restassured.RestAssured.given;

    import java.io.IOException;

    import org.apache.commons.configuration2.ex.ConfigurationException;

    import com.akamai.edgegrid.signer.ClientCredential;
    import com.akamai.edgegrid.signer.EdgeRcClientCredentialProvider;
    import com.akamai.edgegrid.signer.restassured.RestAssuredEdgeGridFilter;

    import io.restassured.response.Response;

    public class GetUserProfile {
        public static void main(String[] args) throws ConfigurationException, IOException {
            ClientCredential credential = EdgeRcClientCredentialProvider
                    .fromEdgeRc("~/.edgerc", "default")
                    .getClientCredential(null);

            Response response = given()
                    .filter(new RestAssuredEdgeGridFilter(credential))
                    .when()
                    .get("/identity-management/v3/user-profile")
                    .then()
                    .extract()
                    .response();
            System.out.println(response.getStatusCode());
            System.out.println(response.asPrettyString());
        }
    }
    ```

> **Note:**
>
> REST-assured doesn't expose certain capabilities effectively. In particular, it doesn't support re-triggering its filters when following a 301/302 redirect. As a result, a redirect becomes an invalid request signature and a rejected request.
>
> If you experience this problem, use the [Apache HTTP Client binding](../edgegrid-signer-apache-http-client) instead of the REST-assured binding. The Apache HTTP Client binding is functionally equivalent and also signs redirected requests properly.