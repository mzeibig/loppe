package de.zeiban.loppe.properties;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyReader {
	
	private static final Logger LOGGER = Logger.getLogger(PropertyReader.class);
	private static Properties properties;
	
	public static synchronized BigDecimal getNumProperty(final String name, final BigDecimal defaultt) {
		if (properties == null) { 
			readProperties();
		}
		final String propString = properties.getProperty(name);
		try {
			final BigDecimal num = new BigDecimal(propString);
			return num;
		} catch (final NumberFormatException nfe) {
			return defaultt;
		}
	}
	
	private static void readProperties() {
		properties = new Properties();
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream("loppe.properties"));
			properties.load(stream);
		} catch (final FileNotFoundException e1) {
			LOGGER.warn("loppe.properties nicht gefunde! Verwende Standardwerte.");
			properties.put("loppe.prozent", "30");
		} catch (final IOException e) {
			LOGGER.error("Fehler beim Zugriff auf loppe.properties:" + e.getMessage(), e);
			properties = null;
		} finally {
			try {stream.close();} catch (final Exception IGNORE) {}
		}
	}

}
