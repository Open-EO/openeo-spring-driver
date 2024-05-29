package org.openeo.spring.dao;

import org.openeo.spring.model.BatchJobResultFeature;
import org.springframework.stereotype.Repository;

@Repository
public class BatchJobResultFeatureDAO extends BatchJobResultDAO<BatchJobResultFeature> {
	
	public BatchJobResultFeatureDAO() {
		super();
	}
}
