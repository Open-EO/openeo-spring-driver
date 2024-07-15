package org.openeo.spring.dao;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import javax.transaction.Transactional;

import org.openeo.spring.model.BatchJobResultFeature;
import org.springframework.stereotype.Repository;

@Repository
public class BatchJobResultFeatureDAO extends BatchJobResultDAO<BatchJobResultFeature> {
	
	public BatchJobResultFeatureDAO() {
		super();
	}
	
    @Override
    @Transactional
    public void saveCapture(Object entity) {
        assertInstanceOf(BatchJobResultFeature.class, entity);
        super.save((BatchJobResultFeature) entity);
    }
}
