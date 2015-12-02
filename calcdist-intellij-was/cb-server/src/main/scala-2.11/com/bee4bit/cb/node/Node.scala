package com.bee4bit.cb.node


class Node(metaInfo:NodeMetaInformation)  {

    var id: String = ""

  var nodeSignalInfo=new NodeSignalInformation()
  var internalId:Long=0
  var nodeConnection: Node=null
  def this(identifier:String,nodeInfo:NodeMetaInformation)={
    this(nodeInfo)
    this.id=identifier
    this.internalId+=1
  }
  def getMetaInformation:NodeMetaInformation={
    metaInfo
  }
   def setNodeSignalInformation(signalInfo:NodeSignalInformation)={
    this.nodeSignalInfo=signalInfo
  }
  def getNodeSignalInformation:NodeSignalInformation={
    nodeSignalInfo
  }

  def connect(nod:Node)={
    if (nod.id!=this.id){
      this.nodeConnection=nod
    }
  }
}

