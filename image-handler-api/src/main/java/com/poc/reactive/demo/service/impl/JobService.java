package com.poc.reactive.demo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poc.reactive.demo.config.MessagingConfiguration.PubsubOutboundGateway;
import com.poc.reactive.demo.contract.AddJobRequest;
import com.poc.reactive.demo.entities.JobEntity;
import com.poc.reactive.demo.entities.JobInput;
import com.poc.reactive.demo.entities.JobOutput;
import com.poc.reactive.demo.exceptions.FileHandlingException;
import com.poc.reactive.demo.messaging.dto.ImageProcessingInputMessage;
import com.poc.reactive.demo.messaging.dto.ImageProcessingOutputMessage;
import com.poc.reactive.demo.repository.IJobRespository;
import com.poc.reactive.demo.service.IFileRepositoryService;
import com.poc.reactive.demo.service.IJobService;

@Service
public class JobService implements IJobService {

	@Autowired
	IJobRespository jobRepository;

	@Autowired
	PubsubOutboundGateway messagePublisher;

	@Autowired
	IFileRepositoryService filesRepository;

	Logger logger = LoggerFactory.getLogger(JobService.class);

	@Override
	public List<JobEntity> get(List<String> uuid) {
		return uuid.stream().map(ui -> {
			Optional<JobEntity> result = this.jobRepository.get(ui);

			return result.orElse(new JobEntity(ui, true, null, new JobOutput(false, "Not found uuid", null)));
		}).collect(Collectors.toList());
	}

	@Override
	public UUID save(AddJobRequest addJob) {
		UUID identifier = UUID.randomUUID();

		logger.info("adding job id: " + identifier);

		try {
			filesRepository.save(addJob.file().getBytes(), addJob.filename());
		} catch (FileHandlingException | IOException e) {
			logger.error("errir handling file upload", e);
			throw new FileHandlingException("unable to upload file", e);
		}

		logger.info("saving job");

		this.jobRepository.save(new JobEntity(identifier.toString(), false,
				new JobInput(addJob.filename(), addJob.resolution().width(), addJob.resolution().height()), null));

		logger.info("dispatching async job for id: " + identifier);

		// dispatch message
		messagePublisher.sendToPubsub(new ImageProcessingInputMessage(identifier.toString(), addJob.type(),
				addJob.filename(), addJob.resolution()));

		return identifier;
	}

	@Override
	public void update(ImageProcessingOutputMessage message) {
		logger.info("updating job id: " + message.jobUUID());
		Optional<JobEntity> result = this.jobRepository.get(message.jobUUID());

		if (result.isEmpty()) {
			throw new RuntimeException("not found entity");
		}

		JobEntity entity = result.get();

		entity.setCompleted(true);
		entity.setOutput(new JobOutput(message.success(), message.errors(), message.outputFilename()));

		this.jobRepository.save(entity);
	}

	@Override
	public byte[] getFile(String filename) {
		return filesRepository.get(filename);
	}

}
