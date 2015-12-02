package com.bee4bit.cd.service


import javax.ws.rs.{Consumes, GET, PUT, Path, PathParam, Produces}

import com.bee4bit.cb.datastoremanager.{DSManager, DataMetaInformation}
import com.bee4bit.cb.node._

@Path("connect")
class Connect {

  val dsManager = DSManager

  var nodeManager = new NodeManager

  @Path("login/{id}")
  @GET
  @Produces(Array("application/json"))
  def nodeSubscribe(@PathParam("id") identifier: String): DataMetaInformation = {
    dsManager.addNode(new Node(identifier, new NodeMetaInformation()))
    dsManager.getMetaInformation

  }

  @Path("dsinfo")
  @GET
  @Produces(Array("application/json"))
  def nodeSubscribe(): DataMetaInformation = {
    dsManager.getMetaInformation
  }

  @Path("signalmsg/{id}")
  @GET
  @Produces(Array("application/json"))
  def nodeSignalMaster(@PathParam("id") id: String): NodeSignalInformation = {
    System.out.println(id)
    new NodeSignalInformation()
  }



  @Path("signalmsg/{id}")
  @PUT
  @Consumes(Array("application/json"))
  def nodeGetSignal(@PathParam("id") id: String, signal: NodeSignalInformation): String = {
    System.out.println("id:" + id)
    System.out.println(signal.getSignal())
    val node = dsManager.getNode(id)
    if (node.isDefined) {
      node.get.setNodeSignalInformation(signal)
      val nodeCompanion=dsManager.getCompanionNode(node.get)
      if (nodeCompanion.isDefined){
        s"${nodeCompanion.get.id}}"
      }
    }


    "{\"signal\": \"ok\"}"

  }
}