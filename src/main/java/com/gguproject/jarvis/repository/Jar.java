package com.gguproject.jarvis.repository;

public class Jar {

	private String name;
	private String version;
	
	public Jar(String name, String version) {
		this.name = name;
		this.version = version;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return this.version;
	}
}
