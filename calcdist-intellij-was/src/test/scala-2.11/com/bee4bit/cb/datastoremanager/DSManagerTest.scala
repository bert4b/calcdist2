package com.bee4bit.cb.datastoremanager


import com.bee4bit.cb.node.{NodeMetaInformation, Node}
import org.scalatest.FunSuite

/**
  * Created by bert on 5-12-2015.
  */
class DSManagerTest extends FunSuite {

  test("testAddNode") {
    val dsManager: DSManager = new DSManager()
    val node:Node=new Node(new NodeMetaInformation)
    dsManager.addNode(node)
  }


}
