package com.bee4bit.cb.node



class Node(metaInfo:NodeMetaInformation)  {

  var id: String = ""
  var nodeSignalInfo=new NodeSignalInformation()
  var internalId:Long=0
  var initiator=true
  var nodeConnection: scala.collection.mutable.Set[Node]=scala.collection.mutable.Set[Node]()
  var websocketSession:String=""

  def this(identifier:String,nodeInfo:NodeMetaInformation)={
    this(nodeInfo)
    this.id=identifier
    this.internalId+=1
  }
  def this(identifier:String,nodeInfo:NodeMetaInformation,initiator:Boolean)={
    this(nodeInfo)
    this.id=identifier
    this.internalId+=1
    this.initiator=initiator
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
    if (nod.id!=this.id && !this.nodeConnection.contains(nod)){
      this.nodeConnection+=nod
    }
  }

  def setSession(session: String): Unit ={
    this.websocketSession=session
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Node]

  override def equals(other: Any): Boolean = other match {
    case that: Node =>
      (that canEqual this) &&
        id == that.id &&
        internalId == that.internalId
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id, internalId)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

