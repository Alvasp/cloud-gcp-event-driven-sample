package com.poc.reactive.demo.entities;

public class JobOutput {

	private boolean success;
	private String errors;
	private String outputFilename;

	public JobOutput() {
	}

	public JobOutput(boolean success, String errors, String outputFilename) {
		super();
		this.success = success;
		this.errors = errors;
		this.outputFilename = outputFilename;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

}
