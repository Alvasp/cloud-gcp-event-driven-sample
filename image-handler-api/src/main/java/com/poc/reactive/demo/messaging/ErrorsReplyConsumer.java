package com.poc.reactive.demo.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.poc.reactive.demo.config.MessagingConfiguration;
import com.poc.reactive.demo.messaging.dto.ImageProcessingOutputMessage;
import com.poc.reactive.demo.service.IJobService;

@Component
public class ErrorsReplyConsumer {
	@Autowired
	private IJobService service;

	@ServiceActivator(inputChannel = MessagingConfiguration.ERRORS_SUB_CHANNEL)
	public void consume(ImageProcessingOutputMessage payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		System.out.println(
				"error received headers={} payload={}" + message.getPubsubMessage().getAttributesMap() + " " + payload);
		// Extract the lastError attribute (if present)
		String lastError = message.getPubsubMessage().getAttributesOrDefault("googclient_deliveryattempt_error",
				"non specified error message");

		service.update(new ImageProcessingOutputMessage(payload.jobUUID(), false, lastError, null));

		// manual ack
		message.ack();
	}
}
