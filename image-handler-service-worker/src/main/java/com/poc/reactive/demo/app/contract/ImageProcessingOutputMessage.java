package com.poc.reactive.demo.app.contract;

public record ImageProcessingOutputMessage(String jobUUID, boolean success, String errors, String outputFilename) {

}
