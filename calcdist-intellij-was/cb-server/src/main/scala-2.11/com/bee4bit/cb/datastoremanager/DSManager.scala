package com.bee4bit.cb.datastoremanager

import javax.websocket.Session

import com.bee4bit.cb.node.{Node, NodeCluster}
import com.bee4bit.cb.notification.EventAdd
import com.bee4bit.cb.server.DSManagerNotification

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
class DSManager {

var notifier=DSManagerNotification

  var clusterSize: Int = 2
  var nodes: mutable.Map[String, Node] = new mutable.HashMap()
  var clusters: ListBuffer[NodeCluster] = new ListBuffer()
  val nodeClusters= new mutable.HashMap[NodeCluster, collection.mutable.Set[Node]]() with mutable.MultiMap[NodeCluster, Node]
  def deleteAll() = {
   nodes.clear()
   clusters.clear()
    nodeClusters.clear()

  }
  def addNode(node: Node) :NodeInClusterInformation = {
    val nodeInfo:NodeInClusterInformation=new NodeInClusterInformation()
    var cluster: NodeCluster = null
    if (nodes.size % clusterSize == 0) {
      cluster = new NodeCluster(clusterSize, clusters.size * clusterSize)
      clusters += cluster
      notifier.event("cluster",EventAdd,cluster)
    }


    //Logic to determine where the node should in some cluster
    val clusterSpace:Long=node.internalId/clusterSize
    val cls = clusters.find((p:NodeCluster)=>p.start>=(clusterSpace *clusterSize) && p.end<=(((clusterSpace+1)*clusterSize)-1))
    if (cls.isDefined){
      val theNodeCluster = cls.get


      nodeClusters.addBinding(theNodeCluster,node)

      if (nodeClusters.get(theNodeCluster).isDefined){
        def f(a:Node,b:Node):Node={
          var n:Node=null

            if (node!=a){
              n=a
            }else if (node!=b){
              n=b
            }
            n
        }
        val theConnectedNode=nodeClusters.get(theNodeCluster).get.reduce(f)

          if (theConnectedNode != null && theConnectedNode!=node){

            node.nodeConnection+=theConnectedNode
            theConnectedNode.nodeConnection+=node

            nodeInfo.nodeConnection +=  theConnectedNode.id
          }else{
            nodeInfo.nodeConnection = scala.collection.mutable.ArrayBuffer()
          }
      }

      nodes.put(node.id, node)

    }
    nodeInfo
  }

  def getMetaInformation: DataMetaInformation = {
    new DataMetaInformation(nodes.size)
  }

  def getNode(id: String): Option[Node] = {

    nodes.get(id)
  }

  def getCompanionNode(node:Node):Option[Node]={
    var nodeComp:Option[Node]=None
    if (nodeClusters.exists(_._2 contains node)) {
      val cluster = nodeClusters.find(_._2 contains node).get._1

      val nodesInCluster:Option[collection.mutable.Set[Node]]=nodeClusters.get(cluster)

      if (nodesInCluster.isDefined) {
        def c2(s: Node) = s.getNodeSignalInformation.answer==null && s.getNodeSignalInformation.getSignal()!=null
        val nodeCompOpt=nodesInCluster.get.find(s=>c2(s))
        if (nodeCompOpt.isDefined){
          nodeComp=nodeCompOpt
        }
      }
    }
    nodeComp
  }

  def deleteNode(session:Session): String ={
    var id: String = null
    val nodeKeyVal:Option[(String, Node)]=nodes.find(_._2.websocketSession==session.getId)
    if (nodeKeyVal.isDefined){
      id=nodeKeyVal.get._2.id
      nodes -= nodeKeyVal.get._1

      val nodeCKeyVal:Option[(NodeCluster,collection.mutable.Set[Node])]=nodeClusters.find(_._2.exists(_.id == id))
      if (nodeCKeyVal.isDefined){

        nodeClusters.remove(nodeCKeyVal.get._1)
        clusters-=nodeCKeyVal.get._1
      }

    }
    id
  }

}

object DSManager extends DSManager{


}