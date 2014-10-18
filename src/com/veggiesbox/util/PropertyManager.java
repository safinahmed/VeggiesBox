package com.veggiesbox.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyManager {
	
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	private static Properties prop = new Properties();
	
	//Static loader for properties files
	static {
		try {
	        //load a properties file from class path, inside static method
			prop.load(new FileInputStream("WEB-INF/config.properties"));
	
		} catch (IOException ex) {
			log.severe("Could not load configuration properties");
	    }
	}
	
	//Returns a requested property
	public static String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	public static int getIntProperty(String key) {
		int result = 0;
		String val = getProperty(key);
		try {
			result = Integer.parseInt(val);
		} catch(NumberFormatException nfe) {
			log.severe("Could convert value " + val + " to int");
		}
		return result;
	}
	
	public static boolean getBoolProperty(String key) {
		return Boolean.parseBoolean(prop.getProperty(key));
	}
}
