package org.openeo.spring.dao;

import org.openeo.spring.model.BatchJobResultCollection;
import org.springframework.stereotype.Repository;

@Repository
public class BatchJobResultCollectionDAO extends BatchJobResultDAO<BatchJobResultCollection> {
	
	public BatchJobResultCollectionDAO() {
		super();
	}
}
