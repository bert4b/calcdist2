package com.bee4bit.cd.websocket
import javax.websocket.server.ServerEndpoint
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.MessageHandler.Whole
@ServerEndpoint("/socket.io")
class WebSocketServer {

  @OnOpen
  def open(session: Session) {
    session.addMessageHandler(FooImpl)
    println(session);
  }

  @OnClose
  def close(session: Session) {
  }

  @OnError
  def onError(error: Throwable) {
    println(error);
  }

  @OnMessage
  def handleMessage(message: String, session: Session) {
    println(session.getId())
    println(message)
  }

}

object FooImpl extends javax.websocket.MessageHandler.Whole[String] {
  override def onMessage(message: String): Unit = {
    println(message);
  }
}