package com.veggiesbox.db.dao;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.veggiesbox.exception.DAOException;
import com.veggiesbox.model.internal.Par;

public class ImagesDAO {
	
	private static final Logger log = Logger.getLogger(ImagesDAO.class.getName());

	private static final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private static final ImagesService imagesService = ImagesServiceFactory.getImagesService();
	
	
	//Method will parde HttpServletRequest and return Blobs Uploaded
	//IMPORTANT: Uploaded photos must be in element name aPhoto
	public static Par<String,BlobKey> getUploadedPhotoUrl(HttpServletRequest hsr) throws DAOException {
		
		Par<String,BlobKey> result = new Par<String,BlobKey>("",null);
		
		try {

			Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(hsr);
			//Methods returns a list of Blobs, but we only use one for now
	        List<BlobKey> blobKey = blobs.get("aPhoto");
	        

	        if(blobKey != null) {	
	        	BlobKey aKey = blobKey.get(0);
		        ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(aKey).secureUrl(false);
		        //This method will return the photo URL
		        result.setFirst(imagesService.getServingUrl(servingOptions));
		        result.setSecond(aKey);
	        }
	        
		} catch(Exception ex) {
			log.severe("Error getting uploaded photo url - " + ex.getMessage());
			throw new DAOException(ex);			
		}
        
        return result;
	}
	
	//Generates a URL to Upload a Photo
	public static String getUploadUrl(String callback) {
		return blobstoreService.createUploadUrl(callback);
	}
	
	//Deletes a photo
	public static void deletePhoto(BlobKey photoKey) {
		try {
			blobstoreService.delete(photoKey);
		} catch(Exception ex) {
			log.severe("Error deleting photo - " + ex.getMessage());
			//Don't throw error if failed
		}
	}
}
