package com.bee4bit.cd.service

import org.scalatest.FunSuite
/**
  * Created by bert on 11-12-2015.
  */
class ConnectTest extends FunSuite {
  test("Connect nodes") {

    val  connect :Connect= new Connect()


    assert(connect.dsManager.clusters.isEmpty)

  }
}
