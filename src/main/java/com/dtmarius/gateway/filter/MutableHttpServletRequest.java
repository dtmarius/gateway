package com.dtmarius.gateway.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

	private HashMap<String, List<String>> headerMap = new HashMap<>();

	public MutableHttpServletRequest(final HttpServletRequest request) {
		super(request);
		ArrayList<String> headerNames = Collections.list(super.getHeaderNames());
		for (String headerName : headerNames) {
			ArrayList<String> headerValues = Collections.list(super.getHeaders(headerName));
			headerMap.put(headerName, headerValues);
		}
	}

	public void setHeader(final String headerName, final String headerValue) {
		headerMap.put(headerName, Arrays.asList(headerValue));
	}

	public void removeHeadersMatchingPattern(final Pattern regexPattern) {
		headerMap.keySet().removeIf(headerName -> regexPattern.matcher(headerName).matches());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeader(String name) {
		if (name == null || headerMap.get(name) == null || headerMap.get(name).size() <= 0)
			return null;
		return headerMap.get(name).get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headerMap.keySet());
	}

	public Set<String> getHeaderNamesAsSet() {
		return headerMap.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<String> getHeaders(String name) {
		if (name == null || headerMap.get(name) == null || headerMap.get(name).size() <= 0)
			return Collections.emptyEnumeration();
		return Collections.enumeration(headerMap.get(name));
	}
}