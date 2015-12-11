package com.bee4bit.cb.datastoremanager

import javax.websocket.Session

import com.bee4bit.cb.node.{NodeCluster, Node}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
class DSManager {

  var clusterSize: Int = 2
  var nodes: mutable.Map[String, Node] = new mutable.HashMap()
  var clusters: ListBuffer[NodeCluster] = new ListBuffer()
  val nodeClusters= new mutable.HashMap[NodeCluster, collection.mutable.Set[Node]]() with mutable.MultiMap[NodeCluster, Node]

  def addNode(node: Node) = {

    var cluster: NodeCluster = null;
    if (nodes.size % clusterSize == 0) {
      cluster = new NodeCluster(clusterSize, clusters.size * clusterSize)
      clusters += cluster
    }


    //Logic to determine where the node should in some cluster
    val clusterSpace:Long=node.internalId/clusterSize
    val cls = clusters.find((p:NodeCluster)=>p.start>=(((clusterSpace))*clusterSize) && p.end<=(((clusterSpace+1)*clusterSize)-1))
    if (cls.isDefined){
      val theNodeCluster = cls.get


      nodeClusters.addBinding(theNodeCluster,node)
      nodes.put(node.id, node)
    }

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
        nodeComp=nodesInCluster.get.find(_.id != node.id)
      }
    }
    nodeComp
  }

  def deleteNode(session:Session): Unit ={
    val nodeKeyVal:Option[(String, Node)]=nodes.find(_._2.websocketSession==session.getId)
    if (nodeKeyVal.isDefined){
      val id:String=nodeKeyVal.get._2.id
      nodes -= nodeKeyVal.get._1

    val nodeCKeyVal:Option[(NodeCluster,collection.mutable.Set[Node])]=nodeClusters.find(_._2.exists(_.id == id))
    if (nodeCKeyVal.isDefined){
      nodeClusters.remove(nodeCKeyVal.get._1)
    }
    }
  }

}

object DSManager extends DSManager{


}