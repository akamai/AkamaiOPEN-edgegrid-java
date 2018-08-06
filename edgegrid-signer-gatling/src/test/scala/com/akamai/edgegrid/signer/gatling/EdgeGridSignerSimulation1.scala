package com.akamai.edgegrid.signer.gatling

import com.akamai.edgegrid.signer.ahc.AsyncHttpClientEdgeGridSignatureCalculator
import io.gatling.core.Predef._
import io.gatling.http.Predef._


class EdgeGridSignerSimulation1 extends Simulation {

  val httpConf = http
    .baseURL("http://localhost")

  val testScenario = scenario("Test scenario")
    .exec(
      http("fakeRequest")
        .get("/test")
        .signatureCalculator(new AsyncHttpClientEdgeGridSignatureCalculator(testdata.testCredential))
    )

  setUp(
    testScenario.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
