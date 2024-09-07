package com.poc.reactive.demo.service;

import com.poc.reactive.demo.exceptions.FileHandlingException;

/**
 * Interface for file handling operations using some repository
 */
public interface IFileRepositoryService {

	public byte[] get(String name) throws FileHandlingException;

	public void save(byte[] content, String name) throws FileHandlingException;
}
