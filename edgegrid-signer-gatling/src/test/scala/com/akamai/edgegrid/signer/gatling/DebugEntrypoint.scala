package com.akamai.edgegrid.signer.gatling

import com.akamai.edgegrid.signer.gatling.simulations.EdgeGridSignerSimulation1
import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object DebugEntrypoint {

  def main(args: Array[String]): Unit = {

    // This sets the class for the Simulation we want to run.
    val simClass = classOf[EdgeGridSignerSimulation1].getName

    val props = new GatlingPropertiesBuilder
    props.simulationClass(simClass)
    Gatling.fromMap(props.build)
  }
}
