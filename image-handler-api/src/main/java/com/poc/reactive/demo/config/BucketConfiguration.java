package com.poc.reactive.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.poc.reactive.demo.utils.EvaluationUtil;

import io.minio.MinioClient;

@Configuration
public class BucketConfiguration {

	@Bean
	MinioClient minioClient() {
		String bucketEndpoint = System.getenv("APP_BUCKET_ENDPOINT");
		String bucketKey = System.getenv("APP_BUCKET_KEY");
		String bucketSecret = System.getenv("APP_BUCKET_SECRET");

		EvaluationUtil.meetOrFail(bucketEndpoint != null,
				"required variable 'APP_BUCKET_ENDPOINT' was not found to init repo service");

		EvaluationUtil.meetOrFail(bucketKey != null,
				"required variable 'APP_BUCKET_KEY' was not found to init repo service");
		EvaluationUtil.meetOrFail(bucketSecret != null,
				"required variable 'APP_BUCKET_SECRET' was not found to init repo service");

		return MinioClient.builder().endpoint(bucketEndpoint).credentials(bucketKey, bucketSecret).build();
	}
}
