package com.poc.reactive.demo.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poc.reactive.demo.exceptions.FileHandlingException;
import com.poc.reactive.demo.services.IFileRepositoryService;
import com.poc.reactive.demo.utils.EvaluationUtil;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

public class BucketRepositoryService implements IFileRepositoryService {
	private final Logger logger = LoggerFactory.getLogger(BucketRepositoryService.class);

	private String bucketEndpoint = System.getenv("APP_BUCKET_ENDPOINT");
	private String bucketKey = System.getenv("APP_BUCKET_KEY");
	private String bucketSecret = System.getenv("APP_BUCKET_SECRET");
	private String bucketName = System.getenv("APP_BUCKET_NAME");
	private String bucketPathIN = System.getenv("APP_BUCKET_INPUT");
	private String bucketPathOUT = System.getenv("APP_BUCKET_OUTPUT");

	private MinioClient minioClient;

	public BucketRepositoryService() {

		System.out.println(System.getenv("app.bucket.endpoint"));

		EvaluationUtil.meetOrFail(this.bucketEndpoint != null,
				"required variable 'APP_BUCKET_ENDPOINT' was not found to init repo service");

		EvaluationUtil.meetOrFail(this.bucketKey != null,
				"required variable 'APP_BUCKET_KEY' was not found to init repo service");

		EvaluationUtil.meetOrFail(this.bucketName != null,
				"required variable 'APP_BUCKET_NAME' was not found to init repo service");

		EvaluationUtil.meetOrFail(this.bucketSecret != null,
				"required variable 'APP_BUCKET_SECRET' was not found to init repo service");

		EvaluationUtil.meetOrFail(this.bucketPathIN != null,
				"required variable 'APP_BUCKET_INPUT' was not found to init repo service");

		EvaluationUtil.meetOrFail(this.bucketPathOUT != null,
				"required variable 'APP_BUCKET_OUTPUT' was not found to init repo service");

		try {
			this.minioClient = MinioClient.builder().endpoint(this.bucketEndpoint) // MinIO server URL
					.credentials(this.bucketKey, this.bucketSecret).build();
		} catch (Exception e) {
			throw new RuntimeException("Error initializing MinIO client: " + e.getMessage(), e);
		}

	}

	@Override
	public byte[] get(String name) throws FileHandlingException {

		System.out.println("getting file from bucket " + bucketName + ", fullpath: " + bucketPathIN + name);

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			GetObjectResponse response = minioClient
					.getObject(GetObjectArgs.builder().bucket(bucketName).object(bucketPathIN + name).build());

			try (InputStream inputStream = response) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesRead);
				}
			}

			return baos.toByteArray();
		} catch (Exception e) {
			throw new FileHandlingException("Error getting file from MinIO: " + e.getMessage(), e);
		}

	}

	@Override
	public void save(byte[] content, String name) throws FileHandlingException {
		try {
			minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(bucketPathOUT + name)
					.stream(new ByteArrayInputStream(content), content.length, -1).build());
		} catch (Exception e) {
			throw new FileHandlingException("Error saving file to MinIO: " + e.getMessage(), e);
		}
	}

}
