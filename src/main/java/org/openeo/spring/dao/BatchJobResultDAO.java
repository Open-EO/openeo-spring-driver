package org.openeo.spring.dao;

import org.openeo.spring.model.BatchJobResult;
import org.springframework.stereotype.Repository;

@Repository
public class BatchJobResultDAO extends AbstractDAO<BatchJobResult> {
	
	public BatchJobResultDAO() {
		setEntityClass(BatchJobResult.class);
	}

}
