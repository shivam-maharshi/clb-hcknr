package com.javacoders.service.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Sample Rest Service.
 * 
 * @author shivam.maharshi
 */
@Path("/hello")
public class RestService {

	@GET
	@Produces("application/text")
	public String hello() {
		return "Hello!";
	}

}
