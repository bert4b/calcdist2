package com.bee4bit.cb.datastoremanager

import com.bee4bit.cb.node.{NodeCluster, Node}
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer
class DSManager {

  var clusterSize: Int = 2;
  var nodes: Map[String, Node] = new HashMap()
  var clusters: ListBuffer[NodeCluster] = new ListBuffer()
  var nodeClusters: Map[NodeCluster, Node] = new HashMap()

  def addNode(node: Node) = {

    var cluster: NodeCluster = null;
    if ((nodes.size) % clusterSize == 0) {
      cluster = new NodeCluster(clusterSize, (clusters.size) * (clusterSize))
      clusters += cluster
    }


    //Logic to determine where the node should in some cluster
    var clusterSpace:Long=node.internalId/clusterSize
    var cls = clusters.find((p:NodeCluster)=>p.start>=(((clusterSpace))*clusterSize) && p.end<=(((clusterSpace+1)*clusterSize)-1))
    if (cls.isDefined){
      val theNodeCluster = cls.get
      nodeClusters.put(theNodeCluster,node)
      nodes.put(node.id, node);
    }

  }

  def getMetaInformation: DataMetaInformation = {
    new DataMetaInformation(nodes.size)
  }

  def getNode(id: String): Option[Node] = {

    nodes.get(id)
  }

  def getCompanionNode(node:Node):Option[Node]={
    val cluster=nodeClusters.find(_._2==node).get._1
    val nodesInCluster:Option[Node]=nodeClusters.get(cluster)

      nodesInCluster

  }

}

object DSManager extends DSManager{


}