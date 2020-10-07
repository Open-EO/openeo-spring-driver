package org.openeo.spring.dao;

import javax.transaction.Transactional;

import org.openeo.spring.model.Job;
import org.springframework.stereotype.Repository;

@Repository
public class JobDAO extends AbstractDAO<Job> {
	
	public JobDAO() {
		setEntityClass(Job.class);
	}
	
	@Transactional
	@Override
	public void save(Job entity) {
		getCurrentSession().save(entity.getProcess());
		getCurrentSession().persist(entity);
	}

}
