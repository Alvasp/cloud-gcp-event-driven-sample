package com.poc.reactive.demo.entities;

public class JobEntity {

	private String uuid;
	private boolean completed;
	private JobInput input;
	private JobOutput output;

	public JobEntity() {
	}

	public JobEntity(String uuid, boolean completed, JobInput input, JobOutput output) {
		super();
		this.uuid = uuid;
		this.completed = completed;
		this.input = input;
		this.output = output;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public JobInput getInput() {
		return input;
	}

	public void setInput(JobInput input) {
		this.input = input;
	}

	public JobOutput getOutput() {
		return output;
	}

	public void setOutput(JobOutput output) {
		this.output = output;
	}

}
