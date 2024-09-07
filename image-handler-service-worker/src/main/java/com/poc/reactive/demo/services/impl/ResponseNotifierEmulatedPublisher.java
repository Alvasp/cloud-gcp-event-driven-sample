package com.poc.reactive.demo.services.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.poc.reactive.demo.app.contract.ImageProcessingOutputMessage;
import com.poc.reactive.demo.exceptions.ResponseEmittingException;
import com.poc.reactive.demo.services.IResponseNotifierService;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ResponseNotifierEmulatedPublisher implements IResponseNotifierService {
	private static final String REPLY_PROJECT_ID = System.getenv("APP_PUBSUB_PROJECTID");
	private static final String REPLOY_TOPIC_ID = System.getenv("APP_PUBSUB_TOPICID");
	private static final String DEV_EMULATOR_HOST = System.getenv("PUBSUB_EMULATOR_HOST");

	@Override
	public void notify(ImageProcessingOutputMessage message) throws ResponseEmittingException {
		ManagedChannel channel = ManagedChannelBuilder.forTarget(DEV_EMULATOR_HOST).usePlaintext().build();
		ExecutorService executor = Executors.newSingleThreadExecutor(); // Custom executor service

		try {
			TransportChannelProvider channelProvider = FixedTransportChannelProvider
					.create(GrpcTransportChannel.create(channel));
			CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

			TopicAdminClient topicClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
					.setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build());

			TopicName topicName = TopicName.of(REPLY_PROJECT_ID, REPLOY_TOPIC_ID);
			Publisher publisher = Publisher.newBuilder(topicName).setChannelProvider(channelProvider)
					.setCredentialsProvider(credentialsProvider).build();

			Gson g = new Gson();
			ByteString data = ByteString.copyFromUtf8(g.toJson(message));
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

			// Once published, returns a server-assigned message id (unique within the
			// topic)
			ApiFuture<String> future = publisher.publish(pubsubMessage);

			// Add an asynchronous callback to handle success / failure
			ApiFutures.addCallback(future, new ApiFutureCallback<String>() {

				@Override
				public void onFailure(Throwable throwable) {
					if (throwable instanceof ApiException) {
						ApiException apiException = ((ApiException) throwable);
						// details on the API exception
						System.out.println(apiException.getStatusCode().getCode());
						System.out.println(apiException.isRetryable());
					}
					System.out.println("Error publishing message : " + throwable);
					throwable.printStackTrace();
				}

				@Override
				public void onSuccess(String messageId) {
					// Once published, returns server-assigned message ids (unique within the topic)
					System.out.println("Published message ID: " + messageId);
				}
			}, MoreExecutors.directExecutor());
			future.get();
			System.out.println("message sent ok");

		} catch (

				IOException | InterruptedException | ExecutionException e) {
			throw new ResponseEmittingException("IOException while creating Publisher: " + e.getMessage(), e);
		} finally {
			try {
				channel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new ResponseEmittingException("Interrupted while shutting down channel: " + e.getMessage(), e);
			}
			executor.shutdown();
		}
	}

}
