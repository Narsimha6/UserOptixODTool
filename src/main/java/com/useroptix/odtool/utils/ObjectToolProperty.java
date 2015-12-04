package com.useroptix.odtool.utils;

import java.util.Properties;

public enum ObjectToolProperty {

	DB_DRIVER,
	DB_USER_NAME,
	DB_PASSWORD,
	DB_URL
	;
	
	private static final String PATH = "/application.properties";
	
//	private static final Logger logger = Logger.getLogger(ObjectToolProperty.class);
	
	private static Properties properties;

	private String value;
	
	private void init() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(ObjectToolProperty.class.getResourceAsStream(PATH));
			}
			catch (Exception e) {
				System.out.println("Unable to load " + PATH + " file from classpath." + e.getMessage());
			}
		}
		value = (String) properties.get(this.toString());
	}

	public String getValue() {
		if (value == null) {
			init();
		}
		return value;
	}
	
}
