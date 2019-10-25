package com.gguproject.jarvis.repository.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationService {

	private static final ConfigurationService instance = new ConfigurationService();
	
	public static ConfigurationService get() {
		return instance;
	}
	
	private Properties properties;
	
	private ConfigurationService() {
		this.properties = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("configuration.properties");
		try{
			this.properties.load(input);
		} catch (IOException ex) {
			throw new IllegalStateException("Can't load configuration property file", ex);
		}
	}
	
	public String getProperty(String name) {
		return this.properties.getProperty(name);
	}
	
	public Integer getPropertyAsInteger(String name) {
		return Integer.valueOf(this.properties.getProperty(name));
	}
	
	public static class PropertyKey {
		public static final String repository = "repository";
		public static final String serverPort = "server.port";
	}
}
