package com.veggiesbox.db.dao;

import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.veggiesbox.exception.DAOException;
import com.veggiesbox.exception.LoginException;
import com.veggiesbox.exception.NotActiveException;
import com.veggiesbox.model.db.AccessToken;
import com.veggiesbox.model.db.User;
import com.veggiesbox.model.db.UserCommunity;
import com.veggiesbox.model.db.UserProfile;
import com.veggiesbox.model.db.UserPurchase;
import com.veggiesbox.util.Constants;
import com.veggiesbox.util.Utils;

public class UserDAO {

	private static final Logger log = Logger.getLogger(UserDAO.class.getName());
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final AsyncDatastoreService aSyncDatastore = DatastoreServiceFactory.getAsyncDatastoreService();

	private final static MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	private final static AsyncMemcacheService aSyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
	/*
	 * 
	 *  USER ENTITY FUNCTION 
	 * 
	 * 
	 */
	
	//Insert a given user
	public static void insert(User user) throws DAOException {
		
		try {

			Entity userEntity = new Entity(User.ENTITY);
	
			userEntity.setProperty("email", user.getEmail());
			userEntity.setProperty("password", user.getPassword());
			userEntity.setProperty("firstName", user.getFirstName());
			userEntity.setProperty("lastName", user.getLastName());
			userEntity.setProperty("referralCount", user.getReferralCount());
			userEntity.setProperty("status", user.getStatus());
			userEntity.setProperty("userSource", user.getUserSource());
			userEntity.setProperty("emailOptIn", user.getEmailOptIn());
			userEntity.setProperty("createdDate", user.getCreatedDate());
			userEntity.setProperty("lastAccessDate", user.getLastAccessDate());
			userEntity.setProperty("referrerKey", user.getReferrerKey());
			
			if(!Utils.isNullOrBlank(user.getPhoto())) {
					userEntity.setUnindexedProperty("photo", user.getPhoto());
			}
			
			Key userKey = datastore.put(userEntity);
			
			user.setKey(userKey);
			
		} catch(Exception ex) {
			log.severe("Error inserting user: " + user.getEmail() + " - " + ex.getMessage());
			throw new DAOException(ex);
		}
	}
	
	//Generic get user from a key
	public static User getUser(Key userKey) throws DAOException {

		User result = new User();
		
		try {
			
			Entity userEntity = datastore.get(userKey);
			
			if(userEntity != null) {
				result.setEmail((String)userEntity.getProperty("email"));
				result.setPassword((String)userEntity.getProperty("password")); 
				result.setFirstName((String)userEntity.getProperty("firstName"));
				result.setLastName((String)userEntity.getProperty("lastName"));
				result.setPhoto((String)userEntity.getProperty("photo"));
				result.setReferralCount((long)userEntity.getProperty("referralCount"));
				result.setStatus((long)userEntity.getProperty("status"));
				//result.setUserSource((long)userEntity.getProperty("userSource"));
				result.setEmailOptIn((long)userEntity.getProperty("emailOptIn"));
				result.setReferrerKey((Key)userEntity.getProperty("referrerKey"));
				//result.setCreatedDate((Date)userEntity.getProperty("createdDate"));
				//result.setLastAccessDate((Date)userEntity.getProperty("lastAccessDate"));
			}
		
		} catch(Exception ex) {
			log.severe("Error getUser - " + ex.getMessage());
			throw new DAOException(ex);			
		}	
		return result;	
	}
	
	//Returns User Key from a given email
	public static Key getKeyFromEmail(String email) throws DAOException {
		
		Key result = null;
		
		try {
			
			Filter emailFilter =  new FilterPredicate("email",
					                      FilterOperator.EQUAL,
					                      email);
			
			Query query = new Query(User.ENTITY).setFilter(emailFilter).setKeysOnly();
			PreparedQuery prepQuery = datastore.prepare(query);
			
			Entity user = prepQuery.asSingleEntity();
			
			if(user != null)
				result = user.getKey();
			
		} catch(Exception ex) {
			log.severe("Error checking user: " + email + " - " + ex.getMessage());
			throw new DAOException(ex);			
		}
		
		return result;
	}
		
	//Increments the referralCount for a given user
	//Special service gets the user again, because of concurrency
	public static User incrementReferralCount(Key userKey) {
		
		User result = new User();
		
		//Begin transaction, because we have to read and write the same value
		Transaction txn = datastore.beginTransaction();
		
		try {
			
			
			//Get entity
			Entity user = datastore.get(userKey);
			
			//Get current referralCount
			long refCount = (long)user.getProperty("referralCount");
			String email = (String)user.getProperty("email");
			
			//Update referralCount
			user.setProperty("referralCount", ++refCount);
			
			//Set value to dB
			datastore.put(user);
			
			//Commit
			txn.commit();
			
			result.setReferralCount(refCount);
			result.setEmail(email);
					
		} catch(Exception ex) {
			log.severe("Error incrementing referral count  - " + ex.getMessage());
			//Don't throw an exception, because activation might have succeeded anyway
		} finally {
			if(txn.isActive())
				txn.rollback();
		}
		
		return result;
	}
	
	//Validates a given email and password, and returns the user Key
	public static Key login(String email, String password) throws DAOException, LoginException, NotActiveException {
		
		try {
			
			//Create query to filter by email
			Filter emailFilter =  new FilterPredicate("email",
					                      FilterOperator.EQUAL,
					                      email);
			
			Query query = new Query(User.ENTITY).setFilter(emailFilter);
			query.addProjection(new PropertyProjection("password", String.class));
			query.addProjection(new PropertyProjection("status",Long.class));

			PreparedQuery prepQuery = datastore.prepare(query);
			
			//Get returned user
			Entity user = prepQuery.asSingleEntity();
			
			//IF there is no user with that email, throw exception
			if(user == null)
				throw new LoginException();
			
			else {
				
				//If user is not active, then don't login
				long status = (long) user.getProperty("status");
				if(status == Constants.USER_STATUS_NEW) 
					throw new NotActiveException();
				
				//Get user password
				String dbPassword = (String) user.getProperty("password");
				
				//Compare with given password
				boolean checkResult = Utils.checkHash(password, dbPassword);
				
				//If passwords match, return key, otherwise throw Exception
				if(checkResult)
					return user.getKey();
				else
					throw new LoginException();
				
			}
			
		} catch(LoginException le) {
			throw new LoginException("Could not login user");
		} catch(NotActiveException nae) {
			throw new NotActiveException("User is not active");
		} catch(Exception ex) {
			log.severe("Error validating login: " + email + " - " + ex.getMessage());
			throw new DAOException(ex);			
		}
	}
	
	//Activates a given user, and returns the Key to it's referrer
	public static void setActive(Key userKey) throws DAOException {
		
		try {

			Entity user = datastore.get(userKey);
			//Update active flag
			user.setProperty("status", Constants.USER_STATUS_ACTIVE);
			aSyncDatastore.put(user);

		
		} catch(Exception ex) {			
			log.severe("Error activating user - " + ex.getMessage());
			throw new DAOException(ex);
		}
	}
		
	//Updates user Last Login
	public static void setLastAccessDate(Key userKey, Date lastLoginDate) throws DAOException {
		try {
			
		    Entity userEntity = datastore.get(userKey);

		    userEntity.setProperty("lastAccessDate", lastLoginDate);
		    //Async update for concurrency
		    aSyncDatastore.put(userEntity);
		    
		} catch(Exception ex) {
			log.severe("Error updateLastAccessDate - " + ex.getMessage());
			throw new DAOException(ex);			
		}
	}	
	
	//Service that replaces a user photo on Datastore, and deletes old Blob
	public static void setPhoto(Key userKey, String photoUrl, BlobKey photoKey) throws DAOException {
		
		try {
			
		    Entity userEntity = datastore.get(userKey);

		    BlobKey oldPhotoKey = (BlobKey) userEntity.getProperty("photoKey");

		    //Set new values
		    userEntity.setUnindexedProperty("photo", photoUrl);
		    userEntity.setUnindexedProperty("photoKey", photoKey);	
		    
		    aSyncDatastore.put(userEntity);
		    
		    //If there was an old photo, delete it
		    if(oldPhotoKey != null) 
		    	ImagesDAO.deletePhoto(oldPhotoKey);
		    
		} catch(Exception ex) {
			log.severe("Error replaceUserPhoto - " + ex.getMessage());
			throw new DAOException(ex);			
		}	
	}
	
	//Service to set the password
	public static void setPassword(Key userKey, String password) throws DAOException {
		
		try {
			
			Entity user = datastore.get(userKey);
			user.setProperty("password", Utils.hashString(password));
			//Async because to use it, user will still have to check the email or logout
			aSyncDatastore.put(user);
			
		} catch(Exception ex) {
			log.severe("Error setting password  - " + ex.getMessage());
			throw new DAOException(ex);
		}
	}
	
	//Save the User Entity part of the User Profile information
	public static void setUserProfile(Key userKey, User user) throws DAOException {
		
		try {
		
		    Entity userEntity = datastore.get(userKey);

		    userEntity.setProperty("firstName", user.getFirstName());
		    userEntity.setProperty("lastName", user.getLastName());
		    
			aSyncDatastore.put(userEntity);
		
		} catch(Exception ex) {
			log.severe("Error saveUserProfile - " + ex.getMessage());
			throw new DAOException(ex);			
		}			
	}
	
	/*
	 * 
	 *  TOKEN ENTITY FUNCTIONS
	 * 
	 * 
	 */
	
	public static AccessToken getAccessToken(Key tokenKey) throws DAOException {
		
		AccessToken result = new AccessToken();
		
		try {
			
			AccessToken cached = (AccessToken) cache.get(tokenKey);
			if(cached != null) {
				result = cached;
				return result;
			}
			
			Entity tokenEntity = datastore.get(tokenKey);
			
			result.setCreatedDate((Date)tokenEntity.getProperty("createdDate"));
			result.setLastUsageDate((Date)tokenEntity.getProperty("lastUsageDate"));
			result.setUserKey((Key) tokenEntity.getProperty("userKey"));
			
			aSyncCache.put(tokenKey, cached);
			
		} catch(Exception ex) {
			log.severe("Error getting - " + ex.getMessage());
			throw new DAOException(ex);
		}
		
		return result;
	}
	
	
	//Inserts an AccessToken
	public static void putAccessToken(AccessToken token, Key tokenKey) throws DAOException {
		
		try {

			Entity tokenEntity = null;
			
			if(tokenKey != null)
				tokenEntity = datastore.get(tokenKey);
			else
				tokenEntity = new Entity(AccessToken.ENTITY);

			//These values are only set on creation, not on update
			if(tokenKey == null) {
				tokenEntity.setProperty("userKey", token.getUserKey());
				tokenEntity.setUnindexedProperty("createdDate", token.getCreatedDate());
			}
			
			tokenEntity.setUnindexedProperty("lastUsageDate", token.getLastUsageDate());
			
			//Add to Datastore
			tokenKey = datastore.put(tokenEntity);
			
			//Put key in object
			token.setKey(tokenKey);
			
		} catch(Exception ex) {
			log.severe("Error inserting token - " + ex.getMessage());
			throw new DAOException(ex);
		}
		
	}
	
	//Delete token by user key, used to delete Token on login (so we don't have multiple tokens)
	public static void deleteToken(Key userKey) throws DAOException {
		
		try {
			
			//Create filter by Key
			Filter userFilter =  new FilterPredicate("userKey",
	                FilterOperator.EQUAL,
	                userKey);
			Query query = new Query(AccessToken.ENTITY).setFilter(userFilter).setKeysOnly();
			
			//Run the query
			PreparedQuery prepQuery = datastore.prepare(query);
			Entity tokenEntity = prepQuery.asSingleEntity();
			
			if(tokenEntity == null)
				return;
			
			//Delete Token
			datastore.delete(tokenEntity.getKey());
		
		} catch(Exception ex) {
			log.severe("Error deleting token by key " + ex.getMessage());
			throw new DAOException(ex);			
		}

	}

	//Get and Encrypt Token to be sendable to a user
	public static String generateTokenFromKey(Key tokenKey, Date creationDate) {
		
		String key = KeyFactory.keyToString(tokenKey);
		key += "_" + Utils.dateToString(creationDate);
		
		//Encrypt result in Strong Algorithm 
		String result = Utils.cryptString(true, key, true);
		
		return result;
	}	
	
	/*
	 * 
	 *  USER PROFILE FUNCTIONS
	 * 
	 * 
	 */
	
	
	public static UserProfile getUserProfile(Key userKey) throws DAOException {
		
		UserProfile result = new UserProfile();
		
		try {
		
			Query profileQuery = new Query(UserProfile.ENTITY).setAncestor(userKey);
	
			Entity userProfileEntity = datastore.prepare(profileQuery).asSingleEntity();
			
			if(userProfileEntity != null) {
				
				//result.setKey(userProfileEntity.getKey());
				result.setGender((String)userProfileEntity.getProperty("gender"));
				result.setBirthDate((Date)userProfileEntity.getProperty("birthDate"));
				result.setPostalCode((String)userProfileEntity.getProperty("postalCode"));
				result.setPostalCodeSpec((String)userProfileEntity.getProperty("postalCodeSpec"));
			}
		
		} catch(Exception ex) {
			log.severe("Error getUserProfile - " + ex.getMessage());
			throw new DAOException(ex);			
		}	
		return result;
		
	}
	
	public static void putUserProfile(Key userKey, UserProfile userProfile) throws DAOException {
		
		try {
		
			Query profileQuery = new Query(UserProfile.ENTITY).setAncestor(userKey);
	
			Entity userProfileEntity = datastore.prepare(profileQuery).asSingleEntity();
			
			if(userProfileEntity == null) {
				userProfileEntity = new Entity(UserProfile.ENTITY,userKey);
			}
			
			userProfileEntity.setProperty("gender", userProfile.getGender());
			userProfileEntity.setProperty("birthDate", userProfile.getBirthDate());
			userProfileEntity.setProperty("postalCode", userProfile.getPostalCode());
			userProfileEntity.setProperty("postalCodeSpec", userProfile.getPostalCodeSpec());
			
			aSyncDatastore.put(userProfileEntity);
		
		} catch(Exception ex) {
			log.severe("Error saveUserProfile - " + ex.getMessage());
			throw new DAOException(ex);			
		}			
	}


	/*
	 * 
	 *  GENERIC USER RELATED FUNCTIONS
	 * 
	 * 
	 */		
	
	//Get the referralId for a given user
    public static String getReferralId(Key referralKey)  { 
    	String key = KeyFactory.keyToString(referralKey);
    	//Encrypts string with ROT13
    	String result = Utils.rot13(key);
    	return result;
    } 
    
    //Get's the Key for a given referral
    public static Key getReferralKey(String referralId, boolean forceCheck) {
    	//Decrypts input string with ROT13
    	referralId = Utils.rot13(referralId);
    	try {
    		Key result = KeyFactory.stringToKey(referralId);
    		//Only getting the user checks if it exists, Key might be valid but non existant
    		if(forceCheck) {
    			Entity user = datastore.get(result);
    		}
    		return result;    	
    	} catch(Exception ex) {
    		log.severe("Error getReferralUser: " + referralId + " - " + ex.getMessage());
    		return null;
    	}
    }
    
	/*
	 * 
	 *  USER DATA FUNCTIONS (MVP)
	 * 
	 * 
	 */		
	
	public static void addUserInvited(Key userKey) throws DAOException {
		
		try {
		
			Entity userEntity = datastore.get(userKey);
			
			if(userEntity != null) {
				userEntity.setProperty("userInvited", 1);
			}
			
			aSyncDatastore.put(userEntity);
		
		} catch(Exception ex) {
			log.severe("Error addUserInvited - " + ex.getMessage());
			throw new DAOException(ex);			
		}			
	}    
	
	public static void addPurchase(UserPurchase userPurchase) throws DAOException {
		
		try {
		
			Entity userPurchaseEntity = new Entity(UserPurchase.ENTITY,userPurchase.getUserKey());
			
			for(Entry<String,Long> entry : userPurchase.getPurchaseValues().entrySet()) {
				userPurchaseEntity.setProperty(entry.getKey(), entry.getValue());
			}
			userPurchaseEntity.setProperty("createdDate", userPurchase.getCreatedDate());
			
			aSyncDatastore.put(userPurchaseEntity);
		
		} catch(Exception ex) {
			log.severe("Error addPurchase - " + ex.getMessage());
			throw new DAOException(ex);			
		}			
	}  
	
	public static void putUserCommunity(UserCommunity userCommunity) throws DAOException {
		
		try {
			
			Entity userCommunityEntity = new Entity(UserCommunity.ENTITY,userCommunity.getUserKey());
		
			userCommunityEntity.setProperty("location", userCommunity.getLocation());
			userCommunityEntity.setProperty("postalCode", userCommunity.getPostalCode());
			
			if(userCommunity.getLocation() == UserCommunity.WORK || userCommunity.getLocation() == UserCommunity.OTHER)
				userCommunityEntity.setUnindexedProperty("extraInfo", userCommunity.getExtraInfo());
			
			aSyncDatastore.put(userCommunityEntity);
		
		} catch(Exception ex) {
			log.severe("Error putUserCommunity - " + ex.getMessage());
			throw new DAOException(ex);			
		}		
	}
	
	public static boolean checkRepliedCommunity(Key userKey) throws DAOException {
		
		boolean result = false;
		
		try {
			
			Query communityQuery = new Query(UserCommunity.ENTITY).setAncestor(userKey).setKeysOnly();
	
			int cnt = datastore.prepare(communityQuery).countEntities(FetchOptions.Builder.withDefaults());
			
			if(cnt > 0)
				result = true;
			
		} catch(Exception ex) {
			log.severe("Error checkRepliedCommunity - " + ex.getMessage());
			throw new DAOException(ex);			
		}	
		
		return result;
	}

}
