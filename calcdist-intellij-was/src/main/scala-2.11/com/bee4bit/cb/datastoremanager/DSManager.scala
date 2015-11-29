package com.bee4bit.cb.datastoremanager

import com.bee4bit.cb.node.Node
import com.bee4bit.cb.node.NodeCluster
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer
class DSManager {
  val clusterSize: Int = 2;
  val nodes: Map[String, Node] = new HashMap()
  val clusters: ListBuffer[NodeCluster] = new ListBuffer()
  val nodeClusters: Map[NodeCluster, Node] = new HashMap()

  def addNode(node: Node) = {

//    var cluster: NodeCluster = null;
//    if (nodes.size % clusterSize == 0) {
//      cluster = new NodeCluster(clusterSize, clusters.size * (clusterSize - 1), clusters.size * clusterSize)
//      clusters += cluster
//    } else {
//      var cls = nodeClusters.keys groupBy (_.start)
//      var nodeClusterO: Option[Iterable[NodeCluster]] = cls.get(node.internalId.intValue() - (node.internalId.intValue() % clusterSize))
//      if (nodeClusterO.isDefined) {
//        var theNodeCluster = nodeClusterO.get.head
//        nodeClusters.put(theNodeCluster, node)
//      }
//    }

    //Logic to determine where the node should in some cluster

    nodes.put(node.id, node);
  }

  def getMetaInformation(): DataMetaInformation = {
    new DataMetaInformation(nodes.size)
  }

  def getNode(id: String): Option[Node] = {

    nodes.get(id)
  }
}

