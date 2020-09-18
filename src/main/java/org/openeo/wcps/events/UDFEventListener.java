package org.openeo.wcps.events;

import java.util.EventListener;

public interface UDFEventListener extends EventListener {
	
	public void udfExecuted(UDFEvent jobEvent);

}
