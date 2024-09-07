package com.poc.reactive.demo.exceptions;

public class FileHandlingException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public FileHandlingException(String message, Throwable t) {
		super(message, t);
	}

}
