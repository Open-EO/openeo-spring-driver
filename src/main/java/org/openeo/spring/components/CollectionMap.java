package org.openeo.spring.components;

import java.util.HashMap;

import org.openeo.spring.model.Collection;
import org.springframework.stereotype.Component;

@Component
public class CollectionMap extends HashMap<String, Collection> {

	private static final long serialVersionUID = 8976376508932782128L;

	public CollectionMap() {
		super();
	}
	
	

}
