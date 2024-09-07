package com.poc.reactive.demo.app.contract;

public record ImageProcessingInputMessage(String jobUUID, JobTypeEnum jobType, String filename,
		ImageProcessingInputResolution resolution) {

}
