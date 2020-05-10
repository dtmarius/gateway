/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author dtmarius.com
 */
public class HttpClientTest {

    private Map<String, List<String>> headerMap;

    @Test
    public void testX() {
        assertTrue(true);
    }

    @Test
    public void testHeaders() {
        String[] headers = new String[] { "a", "b", "c", "d" };

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost/")).headers(headers).build();

        var headerMap = request.headers().map();
        assertEquals("b", reduceHeaderValues(headerMap.get("a")));
        assertEquals("d", reduceHeaderValues(headerMap.get("c")));
    }

    String reduceHeaderValues(List<String> headerValues) {
        return headerValues.stream().collect(Collectors.joining());
    }
}
