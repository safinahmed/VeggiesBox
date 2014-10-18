package com.veggiesbox.controller;

import java.util.logging.Logger;

import com.veggiesbox.db.dao.ProducerDAO;
import com.veggiesbox.exception.DAOException;
import com.veggiesbox.model.ProducerRegistrationRequest;
import com.veggiesbox.model.StatusResult;
import com.veggiesbox.model.db.Producer;
import com.veggiesbox.util.EmailManager;
import com.veggiesbox.util.PropertyManager;

public class ProducerController {
	
	private static final Logger log = Logger.getLogger(ProducerController.class.getName());

	//Controller Service to Register a New Producer
	public static StatusResult register(ProducerRegistrationRequest prr) {
		
		//Default status NOK
		StatusResult result = new StatusResult();
		
		try {
			
			//Transform the RegistrationRequest in a Producer
			Producer producer = new Producer();
			producer.fromObject(prr);
			
			//Insert Producer in DataStore
			ProducerDAO.addProducer(producer);
			
			//Send internal email with new Registration Data
			EmailManager.sendNewProducerEmail(producer.getEmail(), producer);
			
			//Set Status to OK
			result.markOK();
			
		} catch(DAOException dbx) {			
		} catch(Exception ex) {
			log.severe("Error in ProducerController.create - " + ex);
		}		
		
		return result;
	}
}
