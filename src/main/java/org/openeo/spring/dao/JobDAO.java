package org.openeo.spring.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
	
	@Transactional
	public List<Job> findWithOwner(String ownerName) {
		CriteriaBuilder criteria = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Job> query = criteria.createQuery(Job.class);
		Root<Job> from = query.from(Job.class);
	//	CriteriaQuery<T> select = query.select(from); //SELECT * FROM JOBS (get all jobs in the table)
		
		// SELECT * FROM JOBS WHERE owner = "Bahar"; (get all jobs with owner bahar in the table)
		CriteriaQuery<Job> select = query.select(from).where(criteria.equal(from.get("OWNER"), ownerName));
		
		
		
		TypedQuery<Job> typedQuery = getCurrentSession().createQuery(select);
		return typedQuery.getResultList();
		
	}

}
