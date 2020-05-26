package com.dtmarius.gateway.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestWrapperTest extends HttpServletRequestWrapper {

    private Map<String, String> headerMap = new HashMap<>();

    // TODO: figure out how to use the wrapper to set headers
    public HttpServletRequestWrapperTest(final HttpServletRequest request) {
        super(request);
    }

    public void setHeader(final String headerName, final String headerValue) {
        headerMap.put(headerName, headerValue);
    }

    @Override
    public String getHeader(String name) {
        return "";
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return super.getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        // TODO Auto-generated method stub
        return super.getHeaders(name);
    }
}