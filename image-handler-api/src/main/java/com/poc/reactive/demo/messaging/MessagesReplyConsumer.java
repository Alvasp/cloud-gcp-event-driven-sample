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
public class MessagesReplyConsumer {
	@Autowired
	private IJobService service;

	@ServiceActivator(inputChannel = MessagingConfiguration.REPLY_SUB_CHANNEL)
	public void consume(ImageProcessingOutputMessage payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		System.out.println("Message received headers={} payload={}" + message.getPubsubMessage().getAttributesMap()
				+ " " + payload);

		service.update(payload);

		// manual ack
		message.ack();
	}
}
