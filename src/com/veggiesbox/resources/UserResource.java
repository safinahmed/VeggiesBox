package com.veggiesbox.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.Key;
import com.veggiesbox.controller.UserController;
import com.veggiesbox.model.ImageResult;
import com.veggiesbox.model.StatusResult;
import com.veggiesbox.model.UserActivationRequest;
import com.veggiesbox.model.UserAuthData;
import com.veggiesbox.model.UserBarResult;
import com.veggiesbox.model.UserChangePasswordRequest;
import com.veggiesbox.model.UserCommunityRequest;
import com.veggiesbox.model.UserFacebookLoginRequest;
import com.veggiesbox.model.UserFarmResult;
import com.veggiesbox.model.UserLoginRequest;
import com.veggiesbox.model.UserLoginResult;
import com.veggiesbox.model.UserProfileChangeRequest;
import com.veggiesbox.model.UserProfileResult;
import com.veggiesbox.model.UserPurchaseRequest;
import com.veggiesbox.model.UserRegistrationRequest;
import com.veggiesbox.model.UserResendActivationRequest;
import com.veggiesbox.model.UserResetPasswordRequest;
import com.veggiesbox.model.UserSendInvitationRequest;
import com.veggiesbox.model.internal.Par;
import com.veggiesbox.util.Utils;


//Rest path for all User related Services
@Path("/user/")
public class UserResource {

	// Method for Registering a User, receives and replies a JSON
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult register(UserRegistrationRequest urr) {
		//Calls the controller to register the producer, and returns result
		StatusResult result = UserController.register(urr);
		return result;
	}
	
	// Method for Registering a User, receives and replies a JSON
	@POST
	@Path("/facebookLogin")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserLoginResult facebookLogin(UserFacebookLoginRequest uflr) {
		//Calls the controller to register the producer, and returns result
		UserLoginResult result = UserController.facebookLogin(uflr);
		return result;
	}
	
	// Method for Activate a User, receives and replies a JSON
	@POST
	@Path("/activate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult activate(UserActivationRequest uar) {
		//Calls the controller to register the producer, and returns result
		StatusResult result = UserController.activate(uar);
		return result;
	}
	
	// Method for Registering a Producer, receives and replies a JSON
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserLoginResult login(UserLoginRequest ulr) {
		//Calls the controller to register the producer, and returns result
		UserLoginResult result = UserController.login(ulr);
		
		return result;
	}
	
	// Method for Logout a User, receives and replies a JSON
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult logout(UserAuthData uad) {
		
		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(uad);
		
		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {		
			//Calls the controller to register the producer, and returns result
			result = UserController.logout(par.getSecond());
		}
		
		return result;
	}
	
	// Method for Getting Dashboard Information, receives and replies a JSON
	@POST
	@Path("/farm")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserFarmResult farm(UserAuthData uad) {
		
		UserFarmResult result = new UserFarmResult();
		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(uad);
		
		//Set Status to Token Validation Status
		result.setStatus(par.getFirst());
		
		if(result.checkOK()) {		
			//Calls the controller to register the producer, and returns result
			result = UserController.farm(par.getSecond());
		}
		
		return result;
	}
	
	// Method for Getting Dashboard Information, receives and replies a JSON
	@POST
	@Path("/bar")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserBarResult bar(UserAuthData uad) {
		
		UserBarResult result = new UserBarResult();
		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(uad);
		
		//Set Status to Token Validation Status
		result.setStatus(par.getFirst());
		
		if(result.checkOK()) {		
			//Calls the controller to register the producer, and returns result
			result = UserController.bar(par.getSecond());
		}
		
		return result;
	}
	
	// Method called when user wants to reset password, receives and replies a JSON
	@POST
	@Path("/resetPassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult resetPassword(UserResetPasswordRequest fpr) {

		StatusResult result = UserController.resetPassword(fpr);
		return result;
		
	}
	
	// Method called when user wants to change his password, receives and replies a JSON
	@POST
	@Path("/changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult changePassword(UserChangePasswordRequest cpr) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(cpr.getAuthData());
		
		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.changePassword(par.getSecond(),cpr);
		}
		return result;
		
	}
	
	// Method called when user wants to change his password, receives and replies a JSON
	@POST
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserProfileResult profile(UserAuthData uad) {
		
		UserProfileResult result = new UserProfileResult();

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(uad);
		
		result.setStatus(par.getFirst());
		
		if(result.checkOK()) {
			result = UserController.getProfile(par.getSecond());
		}
		return result;
		
	}
	
	// Method called when user wants to change his password, receives and replies a JSON
	@POST
	@Path("/saveProfile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult saveProfile(UserProfileChangeRequest upcr) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(upcr.getAuthData());

		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.saveProfile(par.getSecond(),upcr);
		}
		return result;
		
	}
	
	// Method called when user wants to change his password, receives and replies a JSON
	@POST
	@Path("/sendInvitation")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult sendInvitation(UserSendInvitationRequest usir) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(usir.getAuthData());

		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.sendInvitation(par.getSecond(),usir);
		}
		return result;
		
	}
	
	//Service to upload a photo that will be called from GAE BlobStore
	@POST
	@Path("/photoUpload")
	@Produces(MediaType.APPLICATION_JSON)
	public ImageResult photoUpload(@Context HttpServletRequest hsr) {
		
		ImageResult result = new ImageResult();
		String token = hsr.getParameter("token");
		
		//If there is no token, return error
		if(Utils.isNullOrBlank(token)) {
			return result;			
		}
		
		//Validate Token
		Par<StatusResult,Key> par = UserController.validateToken(new UserAuthData(token));
		
		//Set status, to Token Status
		result.setStatus(par.getFirst());
				
		//Check token validity
		if(result.checkOK()) {		
			//Calls the controller to upload user photo, and returns result
			result = UserController.photoUpload(hsr,par.getSecond());
		}
		
        return result;
	}
	
	//Service to get a Photo Upload URL
	@POST
	@Path("/photoUploadUrl")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ImageResult photoUploadUrl(UserAuthData uad) {
		
		ImageResult result = new ImageResult();

		Par<StatusResult,Key> par = UserController.validateToken(uad);
		
		//Set status, to Token Status
		result.setStatus(par.getFirst());		
		
		//If there is not token, return status
		if(result.checkOK()) {
			result = UserController.photoUploadUrl();	
		}
		
		return result;
	}
	
	//Service to wake up the server
	@GET
	@Path("/awake")
	@Produces(MediaType.TEXT_PLAIN)
	public String awake() {
		return "Ok, ok, I'm awake";
	}

	// Add some temporary data to a user of type USERINFO 
	@POST
	@Path("/setInvited")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult setInvited(UserAuthData uad) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(uad);

		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.addUserInvited(par.getSecond());
		}
		return result;
		
	}
	
	// Add some temporary data to a user of type USERINFO 
	@POST
	@Path("/purchase")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult purchase(UserPurchaseRequest upr) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(upr.getAuthData());

		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.addPurchase(par.getSecond(),upr);
		}
		return result;
		
	}
	
	// Check if user has already replied to the Survey 
	@POST
	@Path("/checkCommunity")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult checkCommunity(UserAuthData uad) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(uad);

		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.userCommunityCheck(par.getSecond());
		}
		return result;		
	}
	
	// Add User reply to the Survey 
	@POST
	@Path("/addCommunity")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult addCommunity(UserCommunityRequest ucr) {

		// TOKEN VALIDATION SYSTEM, should be somewhere like an Interceptor
		Par<StatusResult,Key> par = UserController.validateToken(ucr.getAuthData());

		StatusResult result = par.getFirst();
		
		if(result.checkOK()) {
			result = UserController.userCommunityAdd(ucr,par.getSecond());
		}
		return result;		
	}
	
	// Add User reply to the Survey 
	@POST
	@Path("/resendActivation")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusResult resendActiation(UserResendActivationRequest urar) {
		return UserController.resendActivation(urar);
	}
}


