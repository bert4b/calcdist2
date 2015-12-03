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
    println(session);

  }

  @OnClose
  def close(session: Session): Unit = {
    dsManager.deleteNode(session)
    println(session)
  }

  @OnError
  def onError(error: Throwable) {
    println(error);
  }

  @OnMessage
  def handleMessage(message: String, session: Session) {
    val  gson = new Gson()
    val request:NodeRequest=gson.fromJson(message,classOf[NodeRequest])
    val node=dsManager.getNode(String.valueOf(request.id))

   if (node.isDefined){
     node.get.setSession(session.getId)
     var companion=dsManager.getCompanionNode(node.get).get
     val nodeResponse:NodeResponse=new NodeResponse()
     nodeResponse.companionWith=request.id.toString
     if (companion.id!=request.id.toString){
       //We have a companion


       nodeResponse.nodeSignal=companion.getNodeSignalInformation.getSignal()
      session.getBasicRemote.sendText(gson.toJson(nodeResponse,classOf[NodeResponse]))
     }else{
       session.getBasicRemote.sendText(gson.toJson(nodeResponse,classOf[NodeResponse]))
     }

   }

  }




}

object FooImpl extends javax.websocket.MessageHandler.Whole[String] {
  override def onMessage(message: String): Unit = {
    println(message);
  }

}