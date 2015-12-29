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
  val gson=new Gson()
  @Path("login/{id}")
  @GET
  @Produces(Array("application/json"))
  def nodeSubscribe(@PathParam("id") identifier: String): String = {
    val nodeInf=dsManager.addNode(new Node(identifier, new NodeMetaInformation()))

    val nodeInfo=new NodeMetaInformationResponse()
    nodeInfo.dbVersion=dsManager.getMetaInformation.getDbVersion
    nodeInfo.isInitiator=nodeInf.nodeConnection==null || nodeInf.nodeConnection.isEmpty
    nodeInfo.nodeConnection=nodeInf.nodeConnection.toArray
    nodeInfo.nodeSize=dsManager.clusterSize


    gson.toJson(nodeInfo,classOf[NodeMetaInformationResponse])

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

      def c2(s: Node) = {(s.getNodeSignalInformation.answer==null || s.getNodeSignalInformation.answer.isEmpty) && s.getNodeSignalInformation.getSignal()!=null}
      val nodeSignalOption=node.get.nodeConnection.find(x=>c2(x))
      if (nodeSignalOption.isDefined){
        nodeSignal=nodeSignalOption.get.getNodeSignalInformation
      }

    }

    nodeSignal

  }

  @Path("answersignalmsg/{id}")
  @PUT
  @Consumes(Array("application/json","application/octet-stream"))
   def nodeAnswerSignal(@PathParam("id") id: String, sig: String): String = {
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
  var nodeSigInf:NodeSignalInformation=null

    if (node.isDefined) {

      var run:Boolean=true
      while(run){
        if (node.get.nodeConnection!=null){

          def c2(s: Node) = s.getNodeSignalInformation.answer!=null && s.getNodeSignalInformation.signal!=null
          val nodeSignalOption=node.get.nodeConnection.find(x=>c2(x))
          if (nodeSignalOption.isDefined){
            println(nodeSignalOption.get.nodeSignalInfo.getAnswer())
            nodeSigInf=nodeSignalOption.get.nodeSignalInfo
            if (nodeSignalOption.get.nodeSignalInfo.getAnswer()!="" ){
              run=false
            }
          }




        }
        Thread.sleep(100)
      }
    }

    nodeSigInf
  }
}