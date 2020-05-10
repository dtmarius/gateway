package com.dtmarius.gateway;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpTranslatorTest {

    private HttpTranslator translator;

    @BeforeEach
    private void setup() {
        this.translator = new HttpTranslator();
    }

    @Test
    public void translateHtppServletRequestToHttpRequest_CalledWithNull_ExpectedNull() {
        final HttpRequest httpRequest = translator.translateHtppServletRequestToHttpRequest(null);
        assertEquals(null, httpRequest);
    }

    @Test
    public void translateHtppServletRequestToHttpRequest_CalledWithValidHttpServletRequest_ExpectedValidHttpRequest() {
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        // what todo with the targeturl?
        when(servletRequest.getRequestURL()).thenReturn("value")

        final HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("Host", asList("example.com"));
        headers.put("User-Agent", asList("example/1.0"));
        headers.put("Accept", asList("text/html", "application/json"));
        headers.put("Accept-Language", asList("en", "de"));
        headers.put("Accept-Encoding", asList("utf-8"));
        headers.put("Referer", asList("localhost:8080/demo"));
        headers.put("Connection", asList("keep-alive"));
        headers.put("Upgrade-Insecure-Requests", asList("1"));
        headers.put("If-Modified-Since", asList("Mon, 18 Jul 2019 02:36:04 GMT"));

        final Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        when(servletRequest.getHeaderNames()).thenReturn(headerNames);

        for (String headerName : headers.keySet()) {
            Enumeration<String> headerValues = Collections.enumeration(headers.get(headerName));
            when(servletRequest.getHeaders(headerName)).thenReturn(headerValues);
        }
        HttpRequest actualHttpRequest = translator.translateHtppServletRequestToHttpRequest(servletRequest);

        HttpRequest expectedHttpRequest = HttpRequest.newBuilder();

        assertEquals(expectedHttpRequest, actualHttpRequest);
    }

}