package com.poc.reactive.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poc.reactive.demo.contract.AddJobRequest;
import com.poc.reactive.demo.entities.JobEntity;
import com.poc.reactive.demo.messaging.dto.ImageProcessingInputResolution;
import com.poc.reactive.demo.messaging.dto.JobTypeEnum;
import com.poc.reactive.demo.service.IJobService;

@RestController(value = "jobs")
public class ImageJobController {

	@Autowired
	IJobService service;

	@GetMapping()
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<List<JobEntity>> getJob(@RequestParam String uuids) {

		if (uuids == null || uuids.isBlank() || uuids.isEmpty()) {
			return new ResponseEntity(null, HttpStatusCode.valueOf(422));
		}

		List<String> identifierrs = Arrays.asList(uuids.split(","));

		return new ResponseEntity(service.get(identifierrs), HttpStatusCode.valueOf(200));
	}

	@PostMapping
	public ResponseEntity<String> addJob(@RequestParam MultipartFile file, @RequestParam JobTypeEnum type,
			@RequestParam int width, @RequestParam int height) {

		UUID uuid = service.save(new AddJobRequest(file, type, file.getOriginalFilename(),
				new ImageProcessingInputResolution(height, width)));

		return new ResponseEntity<String>(uuid.toString(), HttpStatusCode.valueOf(200));
	}

	@GetMapping(path = "/file/{name}")
	public ResponseEntity<ByteArrayResource> download(@PathVariable String name) {
		byte[] fileData = service.getFile(name);

		ByteArrayResource resource = new ByteArrayResource(fileData);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return ResponseEntity.ok().headers(headers).contentLength(fileData.length).body(resource);
	}

}
