package com.bee4bit.cd.websocket


import javax.websocket.server.ServerEndpoint
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session

import com.bee4bit.cb.datastoremanager.DSManager
import com.google.gson.Gson

@ServerEndpoint("/socket.io")
class WebSocketServer {

  val dsManager :DSManager =DSManager

  @OnOpen
  def open(session: Session) {
    session.addMessageHandler(FooImpl)
    println(session)

  }

  @OnClose
  def close(session: Session): Unit = {
    val id=dsManager.deleteNode(session)
    println("Deleted:"+id)
  }

  @OnError
  def onError(error: Throwable) {
    println(error)

  }

  @OnMessage
  def handleMessage(message: String, session: Session) {
    val  gson = new Gson()
    val request:NodeRequest=gson.fromJson(message,classOf[NodeRequest])
    val node=dsManager.getNode(String.valueOf(request.id))
    val nodeResponse: NodeResponse = new NodeResponse()
   if (node.isDefined){
     node.get.setSession(session.getId)

     if (dsManager.getCompanionNode(node.get).isDefined) {
       val companion=dsManager.getCompanionNode(node.get).get

       nodeResponse.companionWith = request.id.toString
       if (companion.id != request.id.toString) {
         //We have a companion
         nodeResponse.nodeSignal = companion.getNodeSignalInformation.getSignal()
         nodeResponse.companionWith = companion.id.toString
       }
     }
   }
    session.getBasicRemote.sendText(gson.toJson(nodeResponse, classOf[NodeResponse]))
  }




}

object FooImpl extends javax.websocket.MessageHandler.Whole[String] {
  override def onMessage(message: String): Unit = {
    println(message);
  }

}