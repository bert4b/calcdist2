package com.bee4bit.cb.notification

/**
  * Created by bert on 19-12-2015.
  */
trait Notification {

   def registerSubject(sub:String,f:Unit=>Unit)

}
trait Event{

}
case object EventAdd extends Event{

}
case object EventRemoved extends Event{

}