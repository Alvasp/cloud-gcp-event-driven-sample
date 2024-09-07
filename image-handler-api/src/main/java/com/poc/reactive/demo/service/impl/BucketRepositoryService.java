package com.poc.reactive.demo.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poc.reactive.demo.exceptions.FileHandlingException;
import com.poc.reactive.demo.service.IFileRepositoryService;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class BucketRepositoryService implements IFileRepositoryService {
	private final Logger logger = LoggerFactory.getLogger(BucketRepositoryService.class);

	private String bucketName = System.getenv("APP_BUCKET_NAME");
	private String bucketPathIN = System.getenv("APP_BUCKET_INPUT");
	private String bucketPathOUT = System.getenv("APP_BUCKET_OUTPUT");

	@Autowired
	private MinioClient minioClient;

	@Override
	public byte[] get(String name) throws FileHandlingException {

		System.out.println("getting file from bucket " + bucketName + ", fullpath: " + bucketPathOUT + name);

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			GetObjectResponse response = minioClient
					.getObject(GetObjectArgs.builder().bucket(bucketName).object(bucketPathOUT + name).build());

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
			minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(bucketPathIN + name)
					.stream(new ByteArrayInputStream(content), content.length, -1).build());
		} catch (Exception e) {
			throw new FileHandlingException("Error saving file to MinIO: " + e.getMessage(), e);
		}
	}

}
