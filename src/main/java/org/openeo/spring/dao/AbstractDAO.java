package org.openeo.spring.dao;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDAO<T extends Serializable> {
	
	private final Logger log = LogManager.getLogger(AbstractDAO.class);
	
	private Class<T> entityClass;

	@Autowired
	private SessionFactory sessionFactory;

	public void setEntityClass(Class<T> classToSet) {
		entityClass = classToSet;
	}
	
	@Transactional
	public void save(T entity) {
		log.debug("Save was called for class + " + this.entityClass.getName());
		getCurrentSession().persist(entity);
	}
	
	@Transactional
	public T update(T entity) {
		return (T) getCurrentSession().merge(entity);
	}
	
	@Transactional
	public T findOne(UUID id) {
		return (T) getCurrentSession().get(entityClass, id);
	}
	
	@Transactional
	public T findOne(String id) {
		return (T) getCurrentSession().get(entityClass, id);
	}
	
	@Transactional
	public List<T> findAll() {
		return getCurrentSession().createQuery("from " + entityClass.getName()).list();
	}
	
	@Transactional
	public void delete(T entity) {
		getCurrentSession().delete(entity);
	}
	
	@Transactional
	public void deleteById(UUID id) {
		final T entity = findOne(id);
		delete(entity);
	}
	
	protected final Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
