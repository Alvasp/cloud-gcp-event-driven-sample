package com.poc.reactive.demo.app;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.cloud.functions.CloudEventsFunction;
import com.google.gson.Gson;
import com.poc.reactive.demo.app.contract.ImageProcessingInputMessage;
import com.poc.reactive.demo.app.contract.ImageProcessingOutputMessage;
import com.poc.reactive.demo.app.contract.PubsubBody;
import com.poc.reactive.demo.services.IFileRepositoryService;
import com.poc.reactive.demo.services.IResponseNotifierService;
import com.poc.reactive.demo.services.ITransformationJob;
import com.poc.reactive.demo.services.impl.BucketRepositoryService;
import com.poc.reactive.demo.services.impl.ResponseNotifierFactory;
import com.poc.reactive.demo.services.impl.TransformationJobFactory;

import io.cloudevents.CloudEvent;

public class ImageProcessing implements CloudEventsFunction {

	private IFileRepositoryService fileRepo = new BucketRepositoryService();

	@Override
	public void accept(CloudEvent event) throws Exception {
		System.out.println("starting cloud event function");
		if (event.getData() != null) {
			// Extract Cloud Event data and convert to PubSubBody
			String cloudEventData = new String(event.getData().toBytes(), StandardCharsets.UTF_8);
			Gson gson = new Gson();

			PubsubBody body = gson.fromJson(cloudEventData, PubsubBody.class);

			// Retrieve and decode PubSub message data
			String encodedData = body.getMessage().getData();
			String decodedData = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);

			ImageProcessingInputMessage message = gson.fromJson(decodedData, ImageProcessingInputMessage.class);

			System.out.println("fetching file from bucket");
			// File download
			byte[] fileContent = fileRepo.get(message.filename());

			System.out.println("transformin input image");
			// Transformation
			ITransformationJob job = TransformationJobFactory.getInstance();

			byte[] fileContentModified = job.transform(fileContent, message.resolution());

			// File save
			System.out.println("storing output image");
			String outputFilename = event.getId() + "_" + message.filename();

			fileRepo.save(fileContentModified, outputFilename);

			// Notify response
			IResponseNotifierService notifier = ResponseNotifierFactory.getInstance();

			notifier.notify(new ImageProcessingOutputMessage(message.jobUUID(), true, "", outputFilename));

			System.out.println("success");

		}
	}

}