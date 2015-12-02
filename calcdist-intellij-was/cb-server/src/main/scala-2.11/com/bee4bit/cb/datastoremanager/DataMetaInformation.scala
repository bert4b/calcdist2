package com.bee4bit.cb.datastoremanager

class DataMetaInformation(private var dbVersion:Int) {
 
  var isInitiator:Boolean=true
  
  def getDbVersion:Int={
    dbVersion
  }
  
  
  
}