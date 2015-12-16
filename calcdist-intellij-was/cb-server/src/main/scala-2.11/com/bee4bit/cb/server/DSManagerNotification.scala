package com.bee4bit.cb.server

import java.util

import com.bee4bit.cb.notification.{Event, Notification}

import scala.collection

/**
  * Created by bert on 19-12-2015.
  */
class DSManagerNotification extends Notification {
  var subjects:scala.collection.mutable.Map[String, (Unit) => Unit]=new scala.collection.mutable.HashMap[String,Unit=>Unit]
  def registerSubject(sub:String,f:Unit=>Unit): Unit = {
   subjects.put(sub,f)
  }

  def notifyData(): Unit = {}
}
object DSManagerNotification extends DSManagerNotification{

  def event(subject:String,event:Event,any: Any)={
    var notifications:collection.Map[String, (Unit) => Unit]=subjects.filterKeys(_ ==subject)
    var func:Option[Unit=>Unit]=subjects.get(subject)
    if (func.isDefined){
      func.get.apply()
    }

  }

}