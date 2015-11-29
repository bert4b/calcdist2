package com.bee4bit.cd.ejb

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("info")
class Information {
  
  @Path("info")
  @GET
  @Produces(Array("application/json","application/xml"))
	def info() : String={
		System.out.println("test2");
		return "test"
	}
  
 
}