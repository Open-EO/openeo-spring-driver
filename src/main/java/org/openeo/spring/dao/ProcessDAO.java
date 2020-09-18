package org.openeo.spring.dao;

import org.springframework.stereotype.Repository;
import org.openeo.spring.model.Process;

@Repository
public class ProcessDAO extends AbstractDAO<Process> {
	
	public ProcessDAO() {
		setEntityClass(Process.class);
	}


}
