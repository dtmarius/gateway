package com.dtmarius.gateway;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

public class IncomingHttpRequest {

    private final static Logger log = Logger.getLogger(IncomingHttpRequest.class.getSimpleName());

    private URL url;

    private final String method;

    private final HashMap<String, ArrayList<String>> headerMap;

    private final byte[] body;

    IncomingHttpRequest(final URL url, final String method, final HashMap<String, ArrayList<String>> headerMap,
            final byte[] body) {
        this.url = url;
        this.method = method;
        this.headerMap = headerMap;
        this.body = body;
    }

    public static IncomingHttpRequest ofHttpServletRequest(final HttpServletRequest servletRequest) throws IOException {
        final URL url = new URL(servletRequest.getRequestURL().toString());

        final String method = servletRequest.getMethod();

        final HashMap<String, ArrayList<String>> headerMap = new HashMap<>();
        for (final Iterator<String> it = servletRequest.getHeaderNames().asIterator(); it.hasNext();) {
            final String headerName = it.next();
            final ArrayList<String> headerValues = Collections.list(servletRequest.getHeaders(headerName));
            log.info("toIncommingRequest: " + headerName + ": " + Arrays.toString(headerValues.toArray()));
            headerMap.put(headerName, headerValues);
        }

        final byte[] body = servletRequest.getInputStream().readAllBytes();

        return new IncomingHttpRequest(url, method, headerMap, body);
    }

    public HttpRequest toHttpRequest() throws URISyntaxException, IOException {

        final ArrayList<String> headerList = new ArrayList<>();
        for (final Entry<String, ArrayList<String>> entry : this.getHeaderMap().entrySet()) {
            final String headerName = entry.getKey();
            if (HeaderUtils.isHeaderRestricted(headerName)) {
                continue;
            }
            final String headerValue = entry.getValue().stream().collect(Collectors.joining(", "));
            headerList.add(headerName);
            headerList.add(headerValue);
        }

        final String[] headers = headerList.toArray(new String[headerList.size()]);

        log.info("headers: " + Arrays.toString(headers));

        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(this.getUrl().toURI()).headers(headers)
                .method(this.getMethod(), HttpRequest.BodyPublishers.ofByteArray(this.getBody())).build();
        return httpRequest;
    }

    public URL resolveTargetURL(final Pattern pattern, final String targetURLTemplate) throws MalformedURLException {
        final Matcher matcher = pattern.matcher(this.getUrl().toString());
        if (matcher.find() == false) {
            return null; // TODO: rethink error handling
        }

        final String resolvedTargetURL = matcher.replaceAll(targetURLTemplate);

        URL targetURL = new URL(resolvedTargetURL);
        this.url = targetURL;
        return targetURL;
    }

    public URL getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public HashMap<String, ArrayList<String>> getHeaderMap() {
        return headerMap;
    }

    public byte[] getBody() {
        return body;
    }

}