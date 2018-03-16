package com.teamai.hackathon.core.common;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/* Reading app.properties file */
public class PropertiesReader {

	private static Map<String,PropertiesReader> instanceMap=new HashMap<String, PropertiesReader>();
	
	private static InputStream input = null;
	private static Properties prop = null;
	
	public static PropertiesReader getInstance(String propertiesFileName){
		
		if (!instanceMap.containsKey(propertiesFileName)) {
			instanceMap.put(propertiesFileName, new PropertiesReader(propertiesFileName));
		}
		return instanceMap.get(propertiesFileName);
	}

	public PropertiesReader(String propertiesFileName){
		
			try {
				input = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
				prop = new Properties();
				prop.load(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	public String getProperty(String key){

		String value = null;

		// load a properties file
		try {
			value = prop.getProperty(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

}
