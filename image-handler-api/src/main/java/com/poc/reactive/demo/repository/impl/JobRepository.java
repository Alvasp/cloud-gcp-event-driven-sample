package com.poc.reactive.demo.repository.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.poc.reactive.demo.entities.JobEntity;
import com.poc.reactive.demo.exceptions.DataAccessException;
import com.poc.reactive.demo.repository.IJobRespository;

@Repository
public class JobRepository implements IJobRespository {

	@Autowired
	Firestore db;

	@Override
	public Optional<JobEntity> get(String uuid) throws DataAccessException {

		DocumentReference docRef = db.collection("jobs").document(uuid);

		ApiFuture<DocumentSnapshot> future = docRef.get();

		DocumentSnapshot document;
		try {
			document = future.get();

			if (document.exists()) {
				return Optional.of(document.toObject(JobEntity.class));
			} else {
				return Optional.empty();
			}
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e);
		}
	}

	@Override
	public void save(JobEntity entity) {
		DocumentReference docRef = db.collection("jobs").document(entity.getUuid());

		docRef.set(entity);
	}

}
