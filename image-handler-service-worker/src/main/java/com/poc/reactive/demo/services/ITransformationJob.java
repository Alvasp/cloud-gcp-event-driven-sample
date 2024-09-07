package com.poc.reactive.demo.services;

import com.poc.reactive.demo.app.contract.ImageProcessingInputResolution;
import com.poc.reactive.demo.exceptions.ImageHandlingException;

/**
 * Handles Image transformation job for the specific configuration
 */
public interface ITransformationJob {

	public byte[] transform(byte[] source, ImageProcessingInputResolution configuration) throws ImageHandlingException;

}
