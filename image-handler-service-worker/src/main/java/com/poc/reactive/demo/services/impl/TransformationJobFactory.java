package com.poc.reactive.demo.services.impl;

import com.poc.reactive.demo.exceptions.EvaluateAssertionException;
import com.poc.reactive.demo.services.ITransformationJob;

public abstract class TransformationJobFactory {
	private static final String jobType = System.getenv("APP_JOBTYPE");

	public static ITransformationJob getInstance() throws EvaluateAssertionException {

		if ("resize".equalsIgnoreCase(jobType)) {
			return new TransformationResize();
		}

		if ("watermark".equalsIgnoreCase(jobType)) {
			return new TransformationWaterMark();
		}

		throw new EvaluateAssertionException("invalid job type 'APP_JOBTYPE' specification");
	}

}
