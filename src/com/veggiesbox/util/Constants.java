package com.veggiesbox.util;

public class Constants {

	public final static int STATUS_OK = 0;
	public final static int STATUS_NOK = -1;
	public final static int STATUS_USER_ALREADY_EXISTS = 1;
	public final static int STATUS_INVALID_TOKEN = 2;
	public final static int STATUS_USER_ALREADY_ACTIVE = 3;
	public final static int STATUS_INVALID_LOGIN = 4;
	public final static int STATUS_ALREADY_REPLIED = 5;
	public final static int STATUS_USER_INACTIVE = 6;
	
	public final static int USER_SOURCE_DIRECT = 1;
	public final static int USER_SOURCE_FACEBOOK = 2;
	public static final int USER_STATUS_PROFILE_COMPLETE = 2;
	
	public final static long USER_STATUS_NEW = 0;
	public final static long USER_STATUS_ACTIVE = 1;
	
	public final static boolean IS_DEBUG = PropertyManager.getBoolProperty("isDebug");
	
	
}
