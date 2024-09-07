package com.poc.reactive.demo.entities;

public class JobInput {

	private String filname;
	private int width;
	private int height;

	public JobInput() {
	}

	public JobInput(String filname, int width, int height) {
		super();
		this.filname = filname;
		this.width = width;
		this.height = height;
	}

	public String getFilname() {
		return filname;
	}

	public void setFilname(String filname) {
		this.filname = filname;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
