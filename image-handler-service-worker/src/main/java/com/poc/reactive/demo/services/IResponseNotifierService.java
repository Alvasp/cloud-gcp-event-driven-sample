package com.poc.reactive.demo.services;

import com.poc.reactive.demo.app.contract.ImageProcessingOutputMessage;
import com.poc.reactive.demo.exceptions.ResponseEmittingException;

public interface IResponseNotifierService {

	public void notify(ImageProcessingOutputMessage message) throws ResponseEmittingException;
	
}
