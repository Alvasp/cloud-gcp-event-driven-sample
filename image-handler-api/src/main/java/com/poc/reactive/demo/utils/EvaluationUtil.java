package com.poc.reactive.demo.utils;

import com.poc.reactive.demo.exceptions.EvaluateAssertionException;

public abstract class EvaluationUtil {

	public static void meetOrFail(boolean condition, String message) {
		if (!condition) {
			throw new EvaluateAssertionException(message);
		}
	}

}
