package com.dtmarius.gateway;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CustomHttpServletResponse extends HttpServletResponseWrapper {

    public CustomHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
    }

}