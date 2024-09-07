package com.poc.reactive.demo.messaging.dto;

public record ImageProcessingOutputMessage(String jobUUID, boolean success, String errors, String outputFilename) {

}
