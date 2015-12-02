package com.bee4bit.cb.node

class NodeCluster (sizes:Int,starts:Int){
  var size:Int=sizes
  var start:Int=starts

  var end:Int=(start+(size-1))
}