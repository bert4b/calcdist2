package com.bee4bit.cd.websocket

import javax.websocket._
import javax.websocket.server.ServerEndpoint

import com.bee4bit.cb.datastoremanager.DSManager
import com.google.gson.Gson

/**
  * Created by bert on 18-12-2015.
  */
@ServerEndpoint("/socketconfig.io")
class ConfigurationWebSocketServer {
  val dsManager :DSManager =DSManager
  @OnOpen
  def open(session: Session) {

    println("Config:"+session)

  }

  @OnClose
  def close(session: Session): Unit = {

    println("Config:"+session)
  }

  @OnError
  def onError(error: Throwable) {
    println("Config:"+error)
  }

  @OnMessage
  def handleMessage(message: String, session: Session) {
   if (message=="reset"){
     println("Delete all nodes")
     dsManager.deleteAll()
   }
  }
}
