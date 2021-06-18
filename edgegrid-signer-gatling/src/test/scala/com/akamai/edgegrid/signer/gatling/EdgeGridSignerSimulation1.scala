/*
 * Copyright 2018 Akamai Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akamai.edgegrid.signer.gatling

import com.akamai.edgegrid.signer.ahc.AsyncHttpClientEdgeGridSignatureCalculator
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.gatling.core
import io.gatling.core.Predef._
import io.gatling.http.Predef.{status, http}


class EdgeGridSignerSimulation1 extends Simulation {

  val httpConf = http
    .baseURL("http://" + testdata.SERVICE_MOCK_HOST)

  val wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(testdata.SERVICE_MOCK_PORT))

  val testScenario = core.Predef.scenario("Test scenario")
    .exec(
      http("fakeRequest")
        .get("/test")
        .signatureCalculator(new AsyncHttpClientEdgeGridSignatureCalculator(testdata.testCredential))
        .check(status.is(201))
    )

  before {
    wireMockServer.start()
    wireMockServer.stubFor(get(urlPathEqualTo("/test"))
      .withHeader("Authorization", matching(".*"))
      .withHeader("Host", equalTo(testdata.SERVICE_MOCK))
      .willReturn(aResponse.withStatus(201)
        .withHeader("Content-Type", "text/xml")
        .withBody("<response>Some content</response>")))
  }

  setUp(testScenario.inject(atOnceUsers(1)))
    .protocols(httpConf)
    .assertions(
      global.successfulRequests.percent.is(100)
    )

  after {
    wireMockServer.stop()
  }

}
