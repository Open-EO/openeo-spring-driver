package org.openeo.wcps.events;

import java.util.EventObject;
import java.util.UUID;

public class UDFEvent extends EventObject {
	
	private static final long serialVersionUID = 8813946588128115189L;
	private UUID jobId;

	public UDFEvent(Object source, UUID jobId) {
		super(source);
		this.jobId = jobId;
	}

	public UUID getJobId() {
		return jobId;
	}

}
