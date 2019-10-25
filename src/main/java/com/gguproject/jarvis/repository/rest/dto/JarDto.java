package com.gguproject.jarvis.repository.rest.dto;

public class JarDto {

	private String name;
	
	private String version;
	
	private String jarName;
	
	public JarDto(String name, String version, String jarName) {
		this.name = name;
		this.version = version;
		this.jarName = jarName;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getJarName() {
		return jarName;
	}
}
