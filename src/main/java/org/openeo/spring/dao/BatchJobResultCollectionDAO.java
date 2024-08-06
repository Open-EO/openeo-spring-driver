package org.openeo.spring.dao;

import javax.transaction.Transactional;

import org.openeo.spring.model.BatchJobResultCollection;
import org.springframework.stereotype.Repository;

@Repository
public class BatchJobResultCollectionDAO extends BatchJobResultDAO<BatchJobResultCollection> {
	
	public BatchJobResultCollectionDAO() {
		super();
	}

    @Override
    @Transactional
    public void saveCapture(Object entity) {
        super.save((BatchJobResultCollection) entity);
    }
}
