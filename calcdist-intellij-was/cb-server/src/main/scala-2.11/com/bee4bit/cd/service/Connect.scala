package com.bee4bit.cd.service


import javax.ejb.Asynchronous
import javax.ws.rs.{Consumes, GET, PUT, Path, PathParam, Produces}

import com.bee4bit.cb.datastoremanager.{DSManager, DataMetaInformation}
import com.bee4bit.cb.node._
import com.google.gson.Gson


@Path("connect")
class Connect {

  val dsManager = DSManager

  var nodeManager = new NodeManager

  @Path("login/{id}")
  @GET
  @Produces(Array("application/json"))
  def nodeSubscribe(@PathParam("id") identifier: String): NodeMetaInformationResponse = {
    val nodeInf=dsManager.addNode(new Node(identifier, new NodeMetaInformation()))

    val nodeInfo=new NodeMetaInformationResponse()
    nodeInfo.dbVersion=dsManager.getMetaInformation.getDbVersion
    nodeInfo.isInitiator=nodeInf.nodeConnection==null || nodeInf.nodeConnection==""
    nodeInfo.nodeConnection=nodeInf.nodeConnection
    nodeInfo
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
  @Produces(Array("application/json"))
  def nodeGetSignal(@PathParam("id") id: String, sig: String): String = {
    System.out.println("id:" + id)
    val gson=new Gson()
    val signal=gson.fromJson(sig,classOf[NodeSignalInformation])
    System.out.println(signal.signal)
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

  @Path("receivesignalmsg/{id}")
  @GET
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  def nodeReceiveSignal(@PathParam("id") id: String): NodeSignalInformation = {
    System.out.println("Receive id:" + id)

    val node = dsManager.getNode(id)
    var nodeSignal=new NodeSignalInformation()
    if (node.isDefined) {
      nodeSignal=node.get.nodeConnection.nodeSignalInfo

    }

    nodeSignal

  }

  @Path("answersignalmsg/{id}")
  @PUT
  @Consumes(Array("application/json","application/octet-stream"))
   def nodeAnswerSignal(@PathParam("id") id: String, sig: String): String = {
    val gson=new Gson()
    val signal=gson.fromJson(sig,classOf[NodeSignalInformation])
    System.out.println("Answer id:" + id)

    val node = dsManager.getNode(id)


    if (node.isDefined) {
      node.get.nodeSignalInfo.answer=signal.answer


    }

    "{\"signal\": \"ok\"}"

  }

  @Asynchronous
  @Path("waitforanswer/{id}")
  @GET
  @Consumes(Array("application/json","application/octet-stream"))
  @Produces(Array("application/json"))
  def waitForAnswer(@PathParam("id") id: String):NodeSignalInformation={

    val node = dsManager.getNode(id)


    if (node.isDefined) {

      var run:Boolean=true
      while(run){
        if (node.get.nodeConnection!=null){
          println(node.get.nodeConnection.nodeSignalInfo.getAnswer())
          if (node.get.nodeConnection.nodeSignalInfo.getAnswer()!="" ){
            run=false
          }

        }
        Thread.sleep(100)
      }
    }

    node.get.nodeConnection.nodeSignalInfo
  }
}