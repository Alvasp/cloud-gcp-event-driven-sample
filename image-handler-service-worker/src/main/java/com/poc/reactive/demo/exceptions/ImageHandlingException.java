package com.poc.reactive.demo.exceptions;

public class ImageHandlingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageHandlingException(String message, Throwable e) {
		super(message,e);
	}
	
}
