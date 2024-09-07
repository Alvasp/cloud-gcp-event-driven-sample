package com.poc.reactive.demo.contract;

import org.springframework.web.multipart.MultipartFile;

import com.poc.reactive.demo.messaging.dto.ImageProcessingInputResolution;
import com.poc.reactive.demo.messaging.dto.JobTypeEnum;

public record AddJobRequest(MultipartFile file, JobTypeEnum type, String filename,
		ImageProcessingInputResolution resolution) {

}
