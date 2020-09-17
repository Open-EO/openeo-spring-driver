package org.openeo.wcps.events;

import java.util.EventObject;
import java.util.UUID;

public class JobEvent extends EventObject {
	
	private static final long serialVersionUID = 8813946588128115189L;
	private UUID jobId;

	public JobEvent(Object source, UUID jobId) {
		super(source);
		this.jobId = jobId;
	}

	public UUID getJobId() {
		return jobId;
	}

}
