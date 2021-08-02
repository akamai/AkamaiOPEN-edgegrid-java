package com.akamai.edgegrid.signer.gatling.openapi

import com.akamai.edgegrid.signer.{ClientCredential, EdgeGridV1Signer}
import io.gatling.core.Predef._
import io.gatling.http.Predef.{Request, SignatureCalculator, http}
import io.gatling.http.protocol.HttpProtocolBuilder
import io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION

object OpenApiHttpConfiguration {
  def openApiHttpConf(credential: ClientCredential): HttpProtocolBuilder = http
    .baseUrl("https://" + credential.getHost)
    .sign(new SignatureCalculator {
      override def sign(request: Request): Unit = {
        val requestBuilder = com.akamai.edgegrid.signer.Request.builder.uri(request.getUri.toJavaNetURI)
          .method(request.getMethod.toString)
        if (request.getBody != null) {
          requestBuilder.body(request.getBody.getBytes)
        }

        request.getHeaders.forEach(h => {
          requestBuilder.header(h.getKey, h.getValue)
        })

        val authorization = new EdgeGridV1Signer().getSignature(requestBuilder.build, credential)
        request.getHeaders.set(AUTHORIZATION, authorization)
      }
    })
}
