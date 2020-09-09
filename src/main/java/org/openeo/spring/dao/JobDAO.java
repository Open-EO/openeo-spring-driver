package org.openeo.spring.dao;

import org.openeo.spring.model.Job;
import org.springframework.stereotype.Repository;

@Repository
public class JobDAO extends AbstractDAO<Job> {
	
	public JobDAO() {
		setClazz(Job.class);
	}

}
