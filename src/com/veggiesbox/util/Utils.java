package com.veggiesbox.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

public class Utils {
	
	private static final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,4})$");
	
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	//Crypts a given string, using jasypt
	//encrypt is true for encryption, false for decryption
	//strong is true for Strong encryption, false for Basic encryption
	public static String cryptString(boolean encrypt, String string, boolean strong) {
		
		String result = "";
		TextEncryptor textEncryptor = null;

		if(strong) {
			StrongTextEncryptor st  = new StrongTextEncryptor();
			st.setPassword(PropertyManager.getProperty("cryptPassStrong"));
			textEncryptor = st;
		}
		else {
			BasicTextEncryptor bt = new BasicTextEncryptor();
			bt.setPassword(PropertyManager.getProperty("cryptPassBasic"));
			textEncryptor = bt;
		}
		
		if(encrypt)
			result = textEncryptor.encrypt(string);
		else
			result = textEncryptor.decrypt(string);
		
		return result;
	}
	
	//Hashes a given string, using jasypt
	public static String hashString(String value)  {
		StrongPasswordEncryptor crypt = new StrongPasswordEncryptor();
		return crypt.encryptPassword(value);
	}

	//Checks if passwords match, using jasypt
	public static boolean checkHash(String hash, String stored)
	{
		StrongPasswordEncryptor crypt = new StrongPasswordEncryptor();
		return crypt.checkPassword(hash, stored);
	}

	//Returns formatted date
	public static String dateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS",new Locale("pt","PT"));
		return dateFormat.format(date);
	}
	
	//Returns date from string
	public static Date stringToDate(String date) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS",new Locale("pt","PT"));
		return dateFormat.parse(date);
	}
	
	//Get current Date
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}
	
	//Add days to a date
    public static Date addHours(Date date, int hours)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours); //minus number would decrement the days
        return cal.getTime();
    }
    
    public static String rot13(String value) {
    	
    	  // deal with the case that method is called with null argument
    	  if (value == null) return value;
    	  
    	  // encode plainText
    	  String encodedMessage = "";
    	  for (int i = 0; i < value.length(); i++) {
    	    char c = value.charAt(i);
    	    if      (c >= 'a' && c <= 'm') c += 13;
    	    else if (c >= 'n' && c <= 'z') c -= 13;
    	    else if (c >= 'A' && c <= 'M') c += 13;
    	    else if (c >= 'N' && c <= 'Z') c -= 13;
    	    encodedMessage += c;
    	  }
    	  
    	  return encodedMessage;
    }
    
    //Reads the contents of a file, into a string
    public static String getFileContents(String fileName) {  	
    	try {
    	    BufferedReader br = new BufferedReader(new FileReader(fileName));
    	    StringBuffer str = new StringBuffer();
    	    String line = br.readLine();
    	    while (line != null)
    	    {
    	        str.append(line);
    	        str.append("\n");
    	        line = br.readLine();
    	    }
    	    br.close();
    	    return str.toString();
    	} catch(IOException ex) {
    		log.severe("Could not read file : " + fileName + " - " + ex.getMessage());
    	}
    	return "";
    }
    
	public static boolean isValidEmailAddress(String email) {
		return emailPattern.matcher(email).matches();
	}
    
    public static boolean isNull(String str) {
        return str == null ? true : false;
    }

    public static boolean isNullOrBlank(String param) {
        if (isNull(param) || param.trim().length() == 0) {
            return true;
        }
        return false;
    }
    
}
