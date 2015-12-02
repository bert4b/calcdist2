package com.bee4bit.cb.node

import com.bee4bit.cb.datastoremanager.DSManager

class NodeManager {
  var idCounter:Int=0;
  def registerNode(node: Node,dsManager:DSManager) = {

    node.internalId=idCounter;
    idCounter=idCounter+1
    dsManager.addNode(node)
  }
}

object NodeManager extends NodeManager{

}
