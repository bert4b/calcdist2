package com.bee4bit.cd.service

import com.bee4bit.cb.datastoremanager.{NodeInClusterInformation, DataMetaInformation}
import com.bee4bit.cb.node.NodeMetaInformation

/**
  * Created by bert on 17-12-2015.
  */
class NodeMetaInformationResponse {

  var isInitiator:Boolean=true

  var dbVersion:Int=0

  var nodeConnection:String=""

}
