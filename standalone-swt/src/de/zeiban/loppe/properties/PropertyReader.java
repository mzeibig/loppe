package de.zeiban.loppe.properties;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

public class PropertyReader {
	
	static Properties properties;
	
	public static BigDecimal getNumProperty(String name, BigDecimal defaultt) {
		if (properties == null) { 
			readProperties();
		}
		String propString = properties.getProperty(name);
		try {
			BigDecimal num = new BigDecimal(propString);
			return num;
		} catch (NumberFormatException nfe) {
			return defaultt;
		}
	}
	
	private static void readProperties() {
		properties = new Properties();
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream("loppe.properties"));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
