package com.gguproject.jarvis.repository.service.exception;

public class JarException extends Exception {
	private static final long serialVersionUID = -8360773235711060284L;

	public JarException(String message) {
		super(message);
	}
	
	public JarException(String message, Exception e) {
		super(message, e);
	}
}