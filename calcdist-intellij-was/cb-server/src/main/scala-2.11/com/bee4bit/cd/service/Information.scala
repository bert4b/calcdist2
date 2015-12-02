package com.bee4bit.cd.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

import com.bee4bit.cb.datastoremanager.DSManager

@Path("info")
class Information {
	val dsManager = DSManager
  @Path("info")
  @GET
  @Produces(Array("application/json","application/xml"))
	def info() : String={
		System.out.println("test2");
		return "test"
	}


	@Path("clientcount")
	@GET
	@Produces(Array("application/json","application/xml"))
	def clientcount() : String={
		dsManager.nodes.size.toString
	}

 
}