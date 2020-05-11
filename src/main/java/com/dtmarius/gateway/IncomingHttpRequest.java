package com.dtmarius.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

public class IncomingHttpRequest {

    private final URL url;

    private final String method;

    private final HashMap<String, ArrayList<String>> headerMap;

    private final byte[] body;

    IncomingHttpRequest(final URL url, String method, final HashMap<String, ArrayList<String>> headerMap,
            final byte[] body) {
        this.url = url;
        this.method = method;
        this.headerMap = headerMap;
        this.body = body;
        new StringBuffer(3).append("a");

    }

    public static IncomingHttpRequest ofHttpServletRequest(final HttpServletRequest servletRequest) throws IOException {
        final URL url = new URL(servletRequest.getRequestURL().toString());

        final String method = servletRequest.getMethod();

        final HashMap<String, ArrayList<String>> headerMap = new HashMap<>();
        for (final Iterator<String> it = servletRequest.getHeaderNames().asIterator(); it.hasNext();) {
            final String headerName = it.next();
            final ArrayList<String> headerValues = Collections.list(servletRequest.getHeaders(headerName));
            headerMap.put(headerName, headerValues);
        }

        final byte[] body = servletRequest.getInputStream().readAllBytes();

        return new IncomingHttpRequest(url, method, headerMap, body);
    }

    public HttpRequest toHttpRequest() throws URISyntaxException, IOException {

        final String[] headers = this.getHeaderMap().entrySet().stream().map(entry -> {
            String headerName = entry.getKey();
            String headerValue = entry.getValue().stream().collect(Collectors.joining(", "));
            return Arrays.asList(headerName, headerValue);
        })
                .flatMap(Collection::stream)
                .toArray(String[]::new);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(this.getUrl().toURI())
                .headers(headers)
                .method(this.getMethod(), HttpRequest.BodyPublishers.ofByteArray(this.getBody()))
                .build();
        return httpRequest;
    }

    URL resolveTargetURL(Pattern pattern, String targetURLTemplate) throws MalformedURLException {
        final Matcher matcher = pattern.matcher(this.getUrl().toString());
        if (matcher.find() == false) {
            return null; // TODO: rethink error handling
        }

        final String resolvedTargetURL = matcher.replaceAll(targetURLTemplate);

        return new URL(resolvedTargetURL);
    }

    public URL getUrl() {
        return url;
    }

    public String getMethod() { return method; }

    public HashMap<String, ArrayList<String>> getHeaderMap() {
        return headerMap;
    }

    public byte[] getBody() {
        return body;
    }

}