package org.openeo.wcps.events;

import java.util.EventListener;

public interface JobEventListener extends EventListener {
	
	public void jobQueued(JobEvent jobEvent);
	public void jobExecuted(JobEvent jobEvent);
	public void jobStopped(JobEvent jobEvent);

}
