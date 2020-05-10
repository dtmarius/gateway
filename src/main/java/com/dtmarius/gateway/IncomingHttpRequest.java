package com.dtmarius.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

public class IncomingHttpRequest {

    private final URL url;

    private final HashMap<String, ArrayList<String>> headerMap;

    // evtl. doch byte[]
    private final InputStream contentStream;

    IncomingHttpRequest(final URL url, final HashMap<String, ArrayList<String>> headerMap,
            final InputStream contentStream) {
        this.url = url;
        this.headerMap = headerMap;
        this.contentStream = contentStream;
        new StringBuffer(3).append("a");

    }

    public static IncomingHttpRequest ofHttpServletRequest(final HttpServletRequest servletRequest) throws IOException {
        final URL url = new URL(servletRequest.getRequestURL().toString());
        url.toString();

        final HashMap<String, ArrayList<String>> headerMap = new HashMap<>();
        for (final Iterator<String> it = servletRequest.getHeaderNames().asIterator(); it.hasNext();) {
            final String headerName = it.next();
            final ArrayList<String> headerValues = Collections.list(servletRequest.getHeaders(headerName));
            headerMap.put(headerName, headerValues);
        }

        final InputStream contentStream = servletRequest.getInputStream();

        return new IncomingHttpRequest(url, headerMap, contentStream);
    }

    public URL getUrl() {
        return url;
    }

    public HashMap<String, ArrayList<String>> getHeaderMap() {
        return headerMap;
    }

    public InputStream getContentStream() {
        return contentStream;
    }

}