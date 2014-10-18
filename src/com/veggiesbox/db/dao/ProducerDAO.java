package com.veggiesbox.db.dao;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.veggiesbox.exception.DAOException;
import com.veggiesbox.model.db.Producer;

public class ProducerDAO {

	private static final Logger log = Logger.getLogger(ProducerDAO.class.getName());
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final AsyncDatastoreService aSyncDatastore = DatastoreServiceFactory.getAsyncDatastoreService();
	
	//Method responsible for adding new Producer in DataStore
	public static void addProducer(Producer producer) throws DAOException {
		
		try {

			//Create entity
			Entity producerEntity = new Entity(Producer.ENTITY);

			//Set Entity values
			producerEntity.setProperty("name", producer.getName());
			producerEntity.setUnindexedProperty("postalAddress", producer.getPostalAddress());
			producerEntity.setUnindexedProperty("phoneNumber", producer.getPhoneNumber());
			producerEntity.setProperty("email", producer.getEmail());
			producerEntity.setUnindexedProperty("website", producer.getWebsite());
			producerEntity.setUnindexedProperty("whichProducts", producer.getWhichProducts());
			producerEntity.setUnindexedProperty("createdDate", producer.getCreatedDate());
			
			producerEntity.setProperty("sellGrosso", producer.getSellGrosso());
			producerEntity.setProperty("sellCabaz", producer.getSellCabaz());
			producerEntity.setUnindexedProperty("sellExtra", producer.getSellExtra());
			producerEntity.setProperty("sellFarm", producer.getSellFarm());
			producerEntity.setProperty("sellMercado", producer.getSellMercado());

			//Put data to DS async because we don't need to read it right away
			aSyncDatastore.put(producerEntity);
			
		} catch(Exception ex) {
			log.severe("Error inserting producer: " + producer.getName() + " (" + producer.getEmail() + ") - "+ ex.getMessage());
			throw new DAOException(ex);
		}
	}
}
