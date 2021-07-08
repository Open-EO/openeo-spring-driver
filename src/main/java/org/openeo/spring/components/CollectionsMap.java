package org.openeo.spring.components;

import java.util.HashMap;

import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;
import org.springframework.stereotype.Component;

@Component
public class CollectionsMap extends HashMap<EngineTypes, Collections> {

	private static final long serialVersionUID = 5883454050146323835L;

	public CollectionsMap() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
