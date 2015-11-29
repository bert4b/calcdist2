package com.bee4bit.cb.datastore

case class DatastorePartition(n:String) extends DatastorePartitionBase {
 def dataStoreUUID=n;
}