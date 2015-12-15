package com.bee4bit.cb.node


class NodeSignalInformation {
  var signal:String=""
  var answer:String=""
  def getSignal():String={
    signal
  }
  def setSignal(str:String){
    this.signal=str
  }

  def getAnswer():String={
    answer
  }
  def setAnswer(str:String){
    this.answer=str
  }
}