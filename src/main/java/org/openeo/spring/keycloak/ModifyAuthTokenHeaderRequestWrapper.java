package org.openeo.spring.keycloak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModifyAuthTokenHeaderRequestWrapper extends HttpServletRequestWrapper {

	private final Logger log = LogManager.getLogger(ModifyAuthTokenHeaderRequestWrapper.class);

	/**
	 * construct a wrapper for this request
	 * 
	 * @param request
	 */
	public ModifyAuthTokenHeaderRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	private Map<String, String> headerMap = new HashMap<String, String>();

	/**
	 * add a header with given name and value
	 * 
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value) {
		String previousValue = headerMap.get(name);
		log.debug("The following header is to be added: " + name + " = " + value);
		if (previousValue != null) {
			log.debug("Key already present. Will be removed.");
			headerMap.remove(name);
		}
		headerMap.put(name, value);
	}

	@Override
	public String getHeader(String name) {
		return super.getHeader(headerMap.get(name));
	}

	/**
	 * get the Header names
	 */
	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> names = Collections.list(super.getHeaderNames());
		for (String name : headerMap.keySet()) {
			names.add(name);
			log.trace("header added: " + name);
		}
		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		List<String> values = null;
		if (headerMap.containsKey(name)) {
			values = new ArrayList<String>();
			values.add(headerMap.get(name));
		}else {
			values = Collections.list(super.getHeaders(name));
		}
		return Collections.enumeration(values);
	}
	
	@Override
	 public String getParameter(String name) {
	  String paramValue = super.getParameter(name);
	  if (paramValue == null) {
	   paramValue = headerMap.get(name);
	  }
	  return paramValue;
	 }
}
