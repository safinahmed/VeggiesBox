package com.veggiesbox.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.veggiesbox.controller.ProducerController;
import com.veggiesbox.model.ProducerRegistrationRequest;
import com.veggiesbox.model.StatusResult;


//Rest path for all Prouducer related Services
@Path("/producer/")
public class ProducerResource {

	// Method for Registering a Producer, receives and replies a JSON
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult register(ProducerRegistrationRequest prr) {
		//Calls the controller to register the producer, and returns result
		StatusResult result = ProducerController.register(prr);
		return result;
	}

}
