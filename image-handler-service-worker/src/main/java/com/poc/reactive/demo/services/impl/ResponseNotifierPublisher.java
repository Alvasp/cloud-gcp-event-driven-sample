package com.poc.reactive.demo.services.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Publisher.Builder;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.poc.reactive.demo.app.ImageProcessing;
import com.poc.reactive.demo.app.contract.ImageProcessingOutputMessage;
import com.poc.reactive.demo.exceptions.ResponseEmittingException;
import com.poc.reactive.demo.services.IResponseNotifierService;
import com.poc.reactive.demo.utils.EvaluationUtil;

public class ResponseNotifierPublisher implements IResponseNotifierService {
	private final Logger logger = LoggerFactory.getLogger(ImageProcessing.class);

	private static final String REPLY_PROJECT_ID = System.getenv("APP_PUBSUB_PROJECTID");
	private static final String REPLOY_TOPIC_ID = System.getenv("APP_PUBSUB_TOPICID");

	public ResponseNotifierPublisher() {
		EvaluationUtil.meetOrFail(REPLY_PROJECT_ID != null,
				"required variable 'APP_PUBSUB_PROJECTID' was not found to init repo service");

		EvaluationUtil.meetOrFail(REPLOY_TOPIC_ID != null,
				"required variable 'APP_PUBSUB_TOPICID' was not found to init repo service");
	}

	@Override
	public void notify(ImageProcessingOutputMessage message) throws ResponseEmittingException {
		ProjectTopicName topicName = ProjectTopicName.of(REPLY_PROJECT_ID, REPLOY_TOPIC_ID);
		Publisher publisher = null;

		try {
			Builder builder = Publisher.newBuilder(topicName);

			publisher = builder.build();

			Gson gson = new Gson();

			PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
					.setData(ByteString.copyFromUtf8(gson.toJson(message))).build();

			ApiFuture<String> future = publisher.publish(pubsubMessage);

			ApiFutures.addCallback(future, new ApiFutureCallback<String>() {

				@Override
				public void onFailure(Throwable throwable) {
					if (throwable instanceof ApiException) {
						ApiException apiException = ((ApiException) throwable);
						// details on the API exception
						System.out.println(apiException.getStatusCode().getCode());
						System.out.println(apiException.isRetryable());
					}
					System.out.println("Error publishing message : " + message);
				}

				@Override
				public void onSuccess(String messageId) {
					// Once published, returns server-assigned message ids (unique within the topic)
					System.out.println("Published message ID: " + messageId);
				}

			}, MoreExecutors.directExecutor());
			future.get();
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new ResponseEmittingException("IOException while creating Publisher: " + e.getMessage(), e);
		} finally {
			if (publisher != null) {
				try {
					publisher.shutdown();
					publisher.awaitTermination(1, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					logger.error("Publisher shutdown interrupted: " + e.getMessage());
				}
			}
		}

	}

}
