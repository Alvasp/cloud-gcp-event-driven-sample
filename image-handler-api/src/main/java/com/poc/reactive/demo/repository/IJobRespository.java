package com.poc.reactive.demo.repository;

import java.util.Optional;

import com.poc.reactive.demo.entities.JobEntity;
import com.poc.reactive.demo.exceptions.DataAccessException;

public interface IJobRespository {

	
	public Optional<JobEntity> get(String uuid) throws DataAccessException;
	
	public void save(JobEntity entity);
}
