package com.poc.reactive.demo.exceptions;

public class ResponseEmittingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResponseEmittingException(String message, Throwable t) {
		super(message, t);
	}

}
