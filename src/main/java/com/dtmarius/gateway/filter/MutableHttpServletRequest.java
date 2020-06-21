package com.dtmarius.gateway.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.dtmarius.gateway.utils.HeaderFieldString;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

	private HashMap<HeaderFieldString, List<String>> headerMap = new HashMap<>();

	public MutableHttpServletRequest(final HttpServletRequest request) {
		super(request);
		ArrayList<String> headerNames = Collections.list(super.getHeaderNames());
		for (String headerName : headerNames) {
			ArrayList<String> headerValues = Collections.list(super.getHeaders(headerName));
			headerMap.put(new HeaderFieldString(headerName), headerValues);
		}
	}

	public void setHeader(final String headerName, final String headerValue) {
		headerMap.put(new HeaderFieldString(headerName), Arrays.asList(headerValue));
	}

	public void removeHeadersMatchingPattern(final Pattern pattern) {
		headerMap.keySet().removeIf(headerName -> pattern.matcher(headerName.toString()).matches());
	}

	public void rewriteHeaderValuesMatchingPattern(String headerName, Pattern headerValuePattern,
			String headerValueTemplate) {
		headerMap.computeIfPresent(new HeaderFieldString(headerName), (HeaderFieldString name, List<String> values) -> {
			return values.stream().map(//
					value -> {
						Matcher matcher = headerValuePattern.matcher(value);
						String result = matcher.replaceAll(headerValueTemplate);
						return result;
					}).collect(Collectors.toList());
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeader(String name) {
		HeaderFieldString headerFieldName = new HeaderFieldString(name);
		if (headerFieldName == null //
				|| headerMap.get(headerFieldName) == null //
				|| headerMap.get(headerFieldName).size() <= 0)
			return null;
		return headerMap.get(headerFieldName).get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> keys = headerMap.keySet().stream().map(headerFieldString -> headerFieldString.toString())
				.collect(Collectors.toList());
		return Collections.enumeration(keys);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<String> getHeaders(String name) {
		HeaderFieldString headerFieldName = new HeaderFieldString(name);
		if (headerFieldName == null //
				|| headerMap.get(headerFieldName) == null //
				|| headerMap.get(headerFieldName).size() <= 0)
			return Collections.emptyEnumeration();
		return Collections.enumeration(headerMap.get(headerFieldName));
	}
}