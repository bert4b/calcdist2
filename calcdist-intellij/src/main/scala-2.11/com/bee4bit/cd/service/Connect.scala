package com.bee4bit.cd.service
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.PathParam
import com.bee4bit.cb.datastoremanager.DSManager
import com.bee4bit.cb.node.Node
import com.bee4bit.cb.node.NodeMetaInformation
import com.bee4bit.cb.node.NodeSignalInformation
import com.bee4bit.cb.datastoremanager.DataMetaInformation

@Path("connect")
class Connect {

  val dsManager: DSManager = new DSManager()

  @Path("login/{id}")
  @GET
  @Produces(Array("application/json"))
  def nodeSubscribe(@PathParam("id") identifier: String): DataMetaInformation = {

    dsManager.addNode(new Node(identifier, new NodeMetaInformation()))
    return dsManager.getMetaInformation()

  }

  @Path("dsinfo")
  @GET
  @Produces(Array("application/json","application/xml"))
  def nodeSubscribe(): String = {

    //return dsManager.getMetaInformation()
    return "test"

  }

  @Path("signalmsg/{id}")
  @GET //@Consumes(value=Array("application/json"))
  @Produces(Array("application/json"))
  def nodeSignalMaster(@PathParam("id") id: String): NodeSignalInformation = {
    System.out.println(id)
    return new NodeSignalInformation();

  }

  @Path("signalmsg/{id}")
  @PUT
  @Consumes(Array("application/json")) //@Produces(value=Array("application/json"))
  def nodeGetSignal(@PathParam("id") id: String, signal: NodeSignalInformation): String = {
    System.out.println("id:" + id)
    System.out.println(signal.getSignal())
    var node = dsManager.getNode(id);
    if (node.isDefined) {
      node.get.setNodeSignalInformation(signal);
    }
    return "{\"signal\": \"ok\"}"

  }
}