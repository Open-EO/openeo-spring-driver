package org.openeo.spring.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.openeo.spring.model.BatchJobResult;

/**
 * Shared parent of concrete job result instances.
 */
public abstract class BatchJobResultDAO<T extends BatchJobResult & Serializable> extends AbstractDAO<T> {

	@SuppressWarnings("unchecked")
    public BatchJobResultDAO() {
	    Class<T> type = (Class<T>) ((ParameterizedType)
	            this.getClass()
                .getGenericSuperclass())
	            .getActualTypeArguments()[0];
		setEntityClass(type);
	}
	
	@Override
	// capture conversion
	public void save(T entity) {
	    super.save(entity);
	}

	@Override
	// capture conversion
	public void delete(T entity) {
	    super.delete(entity);
	}
}
