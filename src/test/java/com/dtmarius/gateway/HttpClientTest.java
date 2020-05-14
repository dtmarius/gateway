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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static java.util.Arrays.asList;

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

    // TODO: remove it it will always fail (wrong refernce)
    @Test
    public void testHeaderTransformation() {

        HashMap<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Host", asList("example.com"));
        headerMap.put("User-Agent", asList("example/1.0"));
        headerMap.put("Accept", asList("text/html", "application/json"));
        headerMap.put("Accept-Language", asList("en", "de"));
        // headerMap.put("Accept-Encoding", asList("utf-8"));
        // headerMap.put("Referer", asList("localhost:8080/demo"));
        headerMap.put("Connection", asList("keep-alive"));
        // headerMap.put("Upgrade-Insecure-Requests", asList("1"));
        // headerMap.put("If-Modified-Since", asList("Mon, 18 Jul 2019 02:36:04 GMT"));

        final String[] headers = headerMap.entrySet().stream().map(entry -> {
            String headerName = entry.getKey();
            if (HeaderUtils.isHeaderRestricted(headerName)) {
                return Collections.emptyList();
            }
            String headerValue = entry.getValue().stream().collect(Collectors.joining(","));
            return Arrays.asList(headerName, headerValue);
        }).flatMap(Collection::stream).toArray(String[]::new);
        // headers: [accept-encoding, gzip, deflate, user-agent, vscode-restclient]|#]

        String[] expectedHeaderArray = new String[] { "User-Agent", "example/1.0", "Accept-Language", "en#de" };
        assertEquals(expectedHeaderArray, headers);
    }

}
