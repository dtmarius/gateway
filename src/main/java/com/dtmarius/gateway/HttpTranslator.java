package com.dtmarius.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpTranslator {

//    HttpRequest translateHtppServletRequestToHttpRequest(final HttpServletRequest servletRequest) throws IOException {
//        final ArrayList<String> headerList = new ArrayList<>();
//
//        byte[] body = servletRequest.getInputStream().readAllBytes();
//
//        for(Iterator<String> headerNameIterator = servletRequest.getHeaderNames().asIterator(); headerNameIterator.hasNext();)
//        {
//            final String headerName = headerNameIterator.next();
//
//            final Enumeration<String> headerValues = servletRequest.getHeaders(headerName);
//
//            while(headerValues.hasMoreElements()){
//                final
//            }
//
//            // TODO: move to the filter part
//            // change host header
//            // if (isHeaderRestricted(headerName)) {
//            //     continue;
//            // }
//
//            headerList.add(headerName);
//            headerList.add(headerValue);
//            log.info(headerName + ": " + headerValue);
//        }
//
//        final String[] headers = headerList.toArray(new String[headerList.size()]);
//
//        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofInputStream(() -> {
//            try {
//                final InputStream in = srcRequest.getInputStream();
//                log.info("inputStream Content: " + new String(in.readAllBytes()));
//                return in;
//            } catch (final IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        });
//
//        final HttpRequest request = HttpRequest.newBuilder() //
//                .timeout(Duration.ofSeconds(20))//
//                .uri(URI.create(targetURL)) //
//                .headers(headers)//
//                .method(srcRequest.getMethod(), body)//
//                .build();
//        return request;
//    }

//    HttpServletResponse translateHttpResponseToHttpServletResponse(final HttpResponse<InputStream> httpResponse) {
//        return null;
//    }

}