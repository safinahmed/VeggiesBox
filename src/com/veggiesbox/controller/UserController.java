package com.veggiesbox.controller;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.veggiesbox.db.dao.ImagesDAO;
import com.veggiesbox.db.dao.UserDAO;
import com.veggiesbox.exception.AlreadyActiveException;
import com.veggiesbox.exception.DAOException;
import com.veggiesbox.exception.InvalidTokenException;
import com.veggiesbox.exception.LoginException;
import com.veggiesbox.exception.NotActiveException;
import com.veggiesbox.model.ImageResult;
import com.veggiesbox.model.StatusResult;
import com.veggiesbox.model.UserActivationRequest;
import com.veggiesbox.model.UserAuthData;
import com.veggiesbox.model.UserBarResult;
import com.veggiesbox.model.UserChangePasswordRequest;
import com.veggiesbox.model.UserCommunityRequest;
import com.veggiesbox.model.UserFarmResult;
import com.veggiesbox.model.UserFacebookLoginRequest;
import com.veggiesbox.model.UserLoginRequest;
import com.veggiesbox.model.UserLoginResult;
import com.veggiesbox.model.UserProfileChangeRequest;
import com.veggiesbox.model.UserProfileResult;
import com.veggiesbox.model.UserPurchaseRequest;
import com.veggiesbox.model.UserRegistrationRequest;
import com.veggiesbox.model.UserResendActivationRequest;
import com.veggiesbox.model.UserResetPasswordRequest;
import com.veggiesbox.model.UserSendInvitationRequest;
import com.veggiesbox.model.db.AccessToken;
import com.veggiesbox.model.db.User;
import com.veggiesbox.model.db.UserCommunity;
import com.veggiesbox.model.db.UserProfile;
import com.veggiesbox.model.db.UserPurchase;
import com.veggiesbox.model.internal.Par;
import com.veggiesbox.util.Constants;
import com.veggiesbox.util.EmailManager;
import com.veggiesbox.util.PropertyManager;
import com.veggiesbox.util.RandomString;
import com.veggiesbox.util.Utils;

public class UserController {
	
	private static final Logger log = Logger.getLogger(UserController.class.getName());

	public static StatusResult register(UserRegistrationRequest urr) {
		
		StatusResult result = new StatusResult();
			
		try {
			
			if(!urr.validate())
				return result;
			
			//Check if user with email already exists
			if(UserDAO.getKeyFromEmail(urr.getEmail()) != null) {
				result.setId(Constants.STATUS_USER_ALREADY_EXISTS);
				return result;
			}

			//Create User object
			User user = new User();
			user.fromObject(urr);
			
			//If there is a referralId, set the user
			if(!Utils.isNullOrBlank(urr.getReferrerId())) {
				user.setReferrerKey(UserDAO.getReferralKey(urr.getReferrerId(),true));
			}
			
			//Create User in DB
			UserDAO.insert(user);
			
			Key userKey = user.getKey();
			
			//Activation will be the same as the ReferralId to save time
			String activationId = UserDAO.getReferralId(userKey);
			//Send activation email
			EmailManager.sendActivationEmail(user.getEmail(), activationId);
			
			result.markOK();
			
		} catch(DAOException dbx) {			
		} catch(Exception ex) {
			log.severe("Error in UserController.register - " + ex);
		}		
		
		return result;
	}
	
	//Activates a user
	public static StatusResult activate(UserActivationRequest uar) {
		
		StatusResult result = new StatusResult();
		
		String activationId = uar.getActivationId();		
		
		//Check if there is an activationId
		if(Utils.isNullOrBlank(activationId)) {
			return result;
		}
		
		try {
			//Don't validate user
			Key userKey = UserDAO.getReferralKey(activationId,false);
			User user = UserDAO.getUser(userKey);
			
			if(user.getStatus() > Constants.USER_STATUS_NEW)
				throw new AlreadyActiveException();
			
			
			UserDAO.setActive(userKey);
			
			//Activates the user
			Key referrerKey = user.getReferrerKey();
			
			
			//If there is a referrer, increment the ReferralCount
			if(referrerKey != null) {
				User referrerUser = UserDAO.incrementReferralCount(referrerKey);
			
				//Send email if referral threshold has been reached
				//This is done here to avoid reading the user email if not required
				if(referrerUser.isEmailOptIn() && EmailManager.reachedThreshold(referrerUser.getReferralCount())) {

					EmailManager.sendReferralEmail(referrerUser.getEmail(), referrerUser.getReferralCount());
				}
			}
			
			result.markOK();
			
		} catch(DAOException dbx) {			
		} catch(AlreadyActiveException aae) {
			result.setId(Constants.STATUS_USER_ALREADY_ACTIVE);
		} catch(Exception ex) {
			log.severe("Error in UserController.activate - " + ex);
		}			
		return result;
	}
	
	public static UserLoginResult login(UserLoginRequest ulr) {
		
		UserLoginResult result = new UserLoginResult();
		
		if(!ulr.validate())
			return result;
		
		try {
			
			//Validate username and password, and return key (will throw exception in error)
			Key userKey = UserDAO.login(ulr.getEmail(), ulr.getPassword());
			
			//Login User
			result.getAuthData().setToken(login(userKey));
						
			result.markOK();
			
			
		} catch(DAOException dbx) {			
		} catch(LoginException le) {
			result.setStatus(Constants.STATUS_INVALID_LOGIN);
		} catch(NotActiveException nae) {
			result.setStatus(Constants.STATUS_USER_INACTIVE);
		} catch(Exception ex) {
			log.severe("Error in UserController.loginUser - " + ex);
		}	
		
		return result;
	}
	
	//Login with Facebook service
	public static UserLoginResult facebookLogin(UserFacebookLoginRequest uflr) {
		
		UserLoginResult result = new UserLoginResult();

		try {
			
			//Connect to FB and get user data
			FacebookClient facebookClient = new DefaultFacebookClient(uflr.getFbToken());
			com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);
			Key userKey = UserDAO.getKeyFromEmail(fbUser.getEmail());
			
			//Create User if it doesn't exist
			if(userKey == null) {
				
				User user = new User();
				user.fromObject(fbUser);
				UserDAO.insert(user);
				userKey = user.getKey();
				
				//If there is a referralId, set the user
				if(!Utils.isNullOrBlank(uflr.getReferrerId())) {
					user.setReferrerKey(UserDAO.getReferralKey(uflr.getReferrerId(),true));
				}
				
				UserProfile userProfile = new UserProfile();
				userProfile.fromObject(fbUser);
				UserDAO.putUserProfile(userKey, userProfile);
				
			}
						
			//Login User
			result.getAuthData().setToken(login(userKey));
						
			result.markOK();
		
		} catch(DAOException dbx) {			
		} catch(Exception ex) {
			log.severe("Error in UserController.loginUser - " + ex);
		}	
		
		return result;
	}

	//Private function to do login, no error catched
	private static String login(Key userKey) throws DAOException {
		
		//?? REVISIT
		logout(userKey);

		//Create Token
		AccessToken accessToken = new AccessToken(userKey);
		//Insert New Token in Datastore
		UserDAO.putAccessToken(accessToken,null);
		//Update last login date
		UserDAO.setLastAccessDate(userKey, accessToken.getCreatedDate());
		
		//Crypt token to user
		String token = UserDAO.generateTokenFromKey(accessToken.getKey(), accessToken.getCreatedDate());
		
		return token;
		
	}
	
	//Logout a user
	public static StatusResult logout(Key userKey) {
		
		StatusResult result = new StatusResult(Constants.STATUS_NOK);
		
		try {
		
			//Delete the user Token
			UserDAO.deleteToken(userKey);
			
			result.markOK();
		
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.logout - " + ex);
		}	
		
		return result;
	}
	
	//Function to validate a token
	public static Par<StatusResult,Key> validateToken(UserAuthData uad) {
		
		Par<StatusResult,Key> result = new Par<StatusResult,Key>(new StatusResult(Constants.STATUS_NOK),null);
		
		try {
			
			if(uad == null || !uad.validate())
				return result;
		
			//Decrypt Token
			String decripted = Utils.cryptString(false, uad.getToken(), true);
			
			//Split the Key and Date values
			String[] values = decripted.split("_");
			
			if(Utils.isNullOrBlank(values[0]))
				throw new Exception();
			
			//Get the token for the key
			Key tokenKey = KeyFactory.stringToKey(values[0]);
			AccessToken accessToken = UserDAO.getAccessToken(tokenKey);
			
			//Get the created date on the Token
			Date inTokenDate = Utils.stringToDate(values[1]);
			
			//If dates don't match
			if(!accessToken.getCreatedDate().equals(inTokenDate))
				throw new InvalidTokenException("Token has expired");
			
			Date curDate = Utils.getCurrentDate();
			
			//Add tokenValidity from config to tokenLastUsage
			int tokenValidityHours = PropertyManager.getIntProperty("tokenValidityHours");
			Date tokenLastUsage = Utils.addHours(accessToken.getLastUsageDate(), tokenValidityHours);
			
			//Check if token is within validity, if > 0 then tokenLastUsage is > then curDate
			if(tokenLastUsage.compareTo(curDate) < 0)
				throw new InvalidTokenException("Token has expired");

			accessToken.setLastUsageDate(curDate);
			UserDAO.putAccessToken(accessToken,tokenKey);
			
			//Update last Login date in User
			UserDAO.setLastAccessDate(accessToken.getUserKey(), curDate);
			
			result.getFirst().markOK();
			result.setSecond(accessToken.getUserKey());
		
		} catch(Exception ex) {
			log.severe("Error in UserController.validateToken - " + ex);
			result.getFirst().setId(Constants.STATUS_INVALID_TOKEN);
		}	
		
		return result;
	}
	
	//Upload and Sets User Photo
	public static ImageResult photoUpload(HttpServletRequest hsr, Key userKey) {
		
		ImageResult result = new ImageResult();
		
		try {
			//Get Photo URL and Blob
			Par<String,BlobKey> uploadResult = ImagesDAO.getUploadedPhotoUrl(hsr);
			
			//Replace PhotoURL and Blob from existing user, delete old Blob
			UserDAO.setPhoto(userKey, uploadResult.getFirst(), uploadResult.getSecond());
			
			//Set results
			result.setImageURL(uploadResult.getFirst());
			result.markOK();
			
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.uploadUserPhoto - " + ex);
		}	
     
		return result;
	}
	
	//Return a Photo Upload URL with the Correct Callback
	public static ImageResult photoUploadUrl() {
		
		ImageResult result = new ImageResult();
		try {
			String uploadUrl = ImagesDAO.getUploadUrl(PropertyManager.getProperty("userPhotoUploadCallback"));
			result.setImageURL(uploadUrl);
			result.markOK();
		} catch(Exception ex) {
			log.severe("Error in UserController.getUserPhotoUploadUrl - " + ex);
		}
		return result;
		
	}
	
	//Returns Farm Information for a given user
	public static UserFarmResult farm(Key userKey) {
		
		UserFarmResult result = new UserFarmResult();
		
		try {
			
			User user = UserDAO.getUser(userKey);
						
			result.setReferralCount(user.getReferralCount());
			result.setReferralId(UserDAO.getReferralId(userKey));
			result.markOK();
						
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.farm - " + ex);	
		}
		
		return result;
	}
	
	//Returns UserBar information
	public static UserBarResult bar(Key userKey) {
		
		UserBarResult result = new UserBarResult();
		
		try {
						
			User user = UserDAO.getUser(userKey);
			result.fromObject(user);
			
			result.markOK();
						
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.DashboardResult - " + ex);	
		}
		
		return result;
	}
	
	
	//Service to reset a password
	public static StatusResult resetPassword(UserResetPasswordRequest rpr) {
		
		StatusResult result = new StatusResult();
		
		if(!rpr.validate())
			return result;
		
		try {
			
			//Get User from Email
			Key userKey = UserDAO.getKeyFromEmail(rpr.getEmail());
			
			//If no user found, set error
			if(userKey == null)
				result.setId(Constants.STATUS_INVALID_LOGIN);
			
			else {
				//Generate new Password, set to 12 chars
				String newPassword = RandomString.getNext(12);
				//Set Password in Datastore
				UserDAO.setPassword(userKey, newPassword);
				//Send email
				EmailManager.sendNewPasswordEmail(rpr.getEmail(), newPassword);
				//Set status ok
				result.markOK();
			}
			
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.forgotPassword - " + ex);	
		}
		return result;
		
	}
	
	//Service to reset a password
	public static StatusResult changePassword(Key userKey, UserChangePasswordRequest cpr) {
		
		StatusResult result = new StatusResult();
		
		try {
			
			//Get current DB Password
			User user = UserDAO.getUser(userKey);
			
			//If passwords don't match return error
			if(!Utils.checkHash(cpr.getOldPassword(), user.getPassword()))
				result.setId(Constants.STATUS_INVALID_LOGIN);
			
			else {
				UserDAO.setPassword(userKey, cpr.getNewPassword());
				result.markOK();
			}
			
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.forgotPassword - " + ex);	
		}
		return result;
		
	}
	
	//Service that returns a user profile
	public static UserProfileResult getProfile(Key userKey) {
		
		UserProfileResult result = new UserProfileResult();
		
		try {
			
			User user = UserDAO.getUser(userKey);
			result.fromObject(user);
			
			UserProfile userProfile = UserDAO.getUserProfile(userKey);
			result.fromObject(userProfile);
			
			result.markOK();
						
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.DashboardResult - " + ex);	
		}
		
		return result;

	}

	
	public static StatusResult saveProfile(Key userKey,UserProfileChangeRequest upcr) {

		StatusResult result = new StatusResult();
		
		try {
			
			User user = new User();
			user.fromObject(upcr);
			UserDAO.setUserProfile(userKey, user);			
			
			UserProfile userProfile = new UserProfile();
			userProfile.fromObject(upcr);
			UserDAO.putUserProfile(userKey, userProfile);
			
			result.markOK();
						
		} catch(DAOException de) {
		} catch(Exception ex) {
			log.severe("Error in UserController.DashboardResult - " + ex);	
		}
		
		return result;
	}
	
	public static StatusResult sendInvitation(Key second,
			UserSendInvitationRequest usir) {
		try {
			
			EmailManager.sendInvitationEmail(usir.getEmailList(),usir.getContent());
			
		} catch(Exception ex) {
			log.severe("Error in UserController.sendInvitation - " + ex);
		}
		return null;
	}
	
	
	
	public static StatusResult addUserInvited(Key userKey) {
		StatusResult result = new StatusResult();
		try {	
			UserDAO.addUserInvited(userKey);	
			result.markOK();
		} catch(Exception ex) {
			log.severe("Error in UserController.addUserInvited - " + ex);
		}
		return result;		
	}
	
	public static StatusResult addPurchase(Key userKey, UserPurchaseRequest urr) {
		StatusResult result = new StatusResult();
		
		if(!urr.validate())
			return result;
		
		try {	
			UserPurchase userPurchase = new UserPurchase();
			userPurchase.fromObject(urr);
			userPurchase.setUserKey(userKey);
			UserDAO.addPurchase(userPurchase);	
			result.markOK();
		} catch(Exception ex) {
			log.severe("Error in UserController.addPurchase - " + ex);
		}
		return result;		
	}
	
	public static StatusResult userCommunityCheck(Key userKey) {
		
		StatusResult result = new StatusResult();
		
		try {
			
			if(UserDAO.checkRepliedCommunity(userKey))
				result.setId(Constants.STATUS_ALREADY_REPLIED);
			else
				result.markOK();
			
		} catch(Exception ex) {
			log.severe("Error in UserController.userCommunityStatus - " + ex);
		}
		return result;	
	}
	
	public static StatusResult userCommunityAdd(UserCommunityRequest ucr, Key userKey) {

		StatusResult result = new StatusResult();
		
		if(!ucr.validate())
			return result;
		
		try {
			
			if(UserDAO.checkRepliedCommunity(userKey))
				result.setId(Constants.STATUS_ALREADY_REPLIED);
			else {
				UserCommunity uc = new UserCommunity();
				uc.fromObject(ucr);
				uc.setUserKey(userKey);
				UserDAO.putUserCommunity(uc);
				result.markOK();
			}
			
		} catch(Exception ex) {
			log.severe("Error in UserController.userCommunityStatus - " + ex);
		}
		return result;
		
	}
	
	//Resend activation email
	public static StatusResult resendActivation(UserResendActivationRequest urar) {
		
		StatusResult result = new StatusResult();
		
		if(Utils.isNullOrBlank(urar.getEmail()))
			return result;
		
		try {
			
			Key userKey = UserDAO.getKeyFromEmail(urar.getEmail());
			
			if(userKey == null)
				result.setId(Constants.STATUS_INVALID_LOGIN);
			
			else {
				//Activation will be the same as the ReferralId to save time
				String activationId = UserDAO.getReferralId(userKey);
				//Send activation email
				EmailManager.sendActivationEmail(urar.getEmail(), activationId);				
				result.markOK();
			}
			
		} catch(Exception ex) {
			log.severe("Error in UserController.resendActivation - " + ex);
		}
		return result;
	}
}
