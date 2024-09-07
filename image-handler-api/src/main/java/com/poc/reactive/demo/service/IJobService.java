package com.poc.reactive.demo.service;

import java.util.List;
import java.util.UUID;

import com.poc.reactive.demo.contract.AddJobRequest;
import com.poc.reactive.demo.entities.JobEntity;
import com.poc.reactive.demo.messaging.dto.ImageProcessingOutputMessage;

public interface IJobService {

	List<JobEntity> get(List<String> uiids);

	UUID save(AddJobRequest request);

	void update(ImageProcessingOutputMessage message);

	byte[] getFile(String name);
}
