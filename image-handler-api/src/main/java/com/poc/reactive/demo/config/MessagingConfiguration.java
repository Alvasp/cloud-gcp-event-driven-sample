package com.poc.reactive.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.poc.reactive.demo.messaging.dto.ImageProcessingInputMessage;
import com.poc.reactive.demo.messaging.dto.ImageProcessingOutputMessage;

@Configuration
public class MessagingConfiguration {

	public static final String REPLY_SUB_CHANNEL = "replyChannelSubscription";
	public static final String ERRORS_SUB_CHANNEL = "errorsChannelSubscription";
	public static final String REQUEST_CHANNEL = "requestChannel";

	@Value("${app.pubsub.request-topic}")
	private String requestTopicName;

	@Value("${app.pubsub.reply-subscription}")
	private String replySubscriptionName;

	@Value("${app.pubsub.errors-subscription}")
	private String errorsSubscriptionName;

	@Bean
	@ConditionalOnProperty(name = "APP_PUBSUB_EMULATOR_HOST")
	CredentialsProvider credentialsProvider() {
		return () -> NoCredentialsProvider.create().getCredentials();
	}

	@Bean
	@Primary
	PubSubMessageConverter pubSubMessageConverter(ObjectMapper objectMapper) {
		return new JacksonPubSubMessageConverter(objectMapper);
	}

	@Bean
	MessageChannel replyChannelSubscription() {
		return new PublishSubscribeChannel();
	}

	@Bean
	MessageChannel errorsChannelSubscription() {
		return new PublishSubscribeChannel();
	}

	@Bean
	PubSubInboundChannelAdapter inboundReplyChannelAdapter(@Qualifier(REPLY_SUB_CHANNEL) MessageChannel messageChannel,
			PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, replySubscriptionName);
		adapter.setOutputChannel(messageChannel);
		adapter.setAckMode(AckMode.MANUAL);
		adapter.setPayloadType(ImageProcessingOutputMessage.class);
		return adapter;
	}

	@Bean
	PubSubInboundChannelAdapter inboundErrorChannelAdapter(@Qualifier(ERRORS_SUB_CHANNEL) MessageChannel messageChannel,
			PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, errorsSubscriptionName);
		adapter.setOutputChannel(messageChannel);
		adapter.setAckMode(AckMode.MANUAL);
		adapter.setPayloadType(ImageProcessingOutputMessage.class);
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = REQUEST_CHANNEL)
	MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
		return new PubSubMessageHandler(pubsubTemplate, requestTopicName);
	}

	@MessagingGateway(defaultRequestChannel = REQUEST_CHANNEL)
	public interface PubsubOutboundGateway {
		default void sendToPubsub(ImageProcessingInputMessage message) {
			Message<ImageProcessingInputMessage> pubsubMessage = MessageBuilder.withPayload(message)
					.setHeader("jobType", message.jobType().toString()).build();

			send(pubsubMessage);
		}

		void send(Message<?> message);

	}
}
