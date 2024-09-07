package com.poc.reactive.demo.exceptions;

public class DataAccessException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DataAccessException(String message, Throwable t) {
		super(message, t);
	}

}
