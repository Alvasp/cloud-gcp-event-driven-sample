package com.poc.reactive.demo.messaging.dto;

public record ImageProcessingInputMessage(String jobUUID, JobTypeEnum jobType, String filename, ImageProcessingInputResolution resolution) {

}
