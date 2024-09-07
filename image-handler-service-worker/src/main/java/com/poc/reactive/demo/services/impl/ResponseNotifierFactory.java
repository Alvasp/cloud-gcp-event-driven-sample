package com.poc.reactive.demo.services.impl;

import com.poc.reactive.demo.services.IResponseNotifierService;

public abstract class ResponseNotifierFactory {
	private static final String DEV_EMULATOR_HOST = System.getenv("PUBSUB_EMULATOR_HOST");

	public static IResponseNotifierService getInstance() {

		if (DEV_EMULATOR_HOST == null || DEV_EMULATOR_HOST.isBlank() || DEV_EMULATOR_HOST.isEmpty()) {
			return new ResponseNotifierPublisher();
		}

		System.out.println("Returning emulator instance");
		return new ResponseNotifierEmulatedPublisher();

	}

}
