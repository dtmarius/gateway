package com.dtmarius.gateway.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ModifyableHttpServletRequest extends HttpServletRequestWrapper {

    private HashMap<String, List<String>> headerMap = new HashMap<>();

    public ModifyableHttpServletRequest(final HttpServletRequest request) {
        super(request);
        for (final Iterator<String> it = super.getHeaderNames().asIterator(); it.hasNext();) {
            final String headerName = it.next();
            final ArrayList<String> headerValues = Collections.list(super.getHeaders(headerName));
            headerMap.put(headerName, headerValues);
        }
    }

    public void setHeader(final String headerName, final String headerValue) {
        headerMap.put(headerName, Arrays.asList(headerValue));
    }

    public void removeHeader(final String headerName) {
        headerMap.remove(headerName);
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