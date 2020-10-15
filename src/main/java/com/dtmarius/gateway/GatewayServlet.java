/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GatewayServlet is used to proxy a request from a to b and back. <br>
 * The restricted headers connection, content-length, expect, host and upgrade
 * are automatically set by the application server.
 * 
 * If the response contains a body and the status code is not successful, the
 * status code 200 is returned.
 * 
 * Headers can be processed stream up and down by various servlet filters.
 */
public class GatewayServlet extends HttpServlet {

    private static final long serialVersionUID = -9096942981327154354L;

    private final Logger log = Logger.getLogger(GatewayServlet.class.getSimpleName());

    private String requestURLRegex;
    private String targetURLTemplate;

    private Pattern pattern;

    @Override
    public void init() throws ServletException {
        this.requestURLRegex = this.getInitParameter("requestURLRegex");
        this.targetURLTemplate = this.getInitParameter("targetURLTemplate");
        this.pattern = Pattern.compile(this.requestURLRegex);
    }

    protected String resolveDestinationURL(final Pattern pattern, final String targetURLTemplate,
            final String sourceURL) throws MalformedURLException {

        final Matcher matcher = pattern.matcher(sourceURL);
        if (matcher.find() == false) {
            throw new IllegalStateException(
                    "Destination url could not be determined, check the configuration for the servlet "
                            + this.getServletName());
        }

        final String resolvedTargetURL = matcher.replaceAll(targetURLTemplate);
        return resolvedTargetURL;
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        HttpRequestDto requestDto = HttpRequestMapper.mapHttpServletRequestToHttpRequestDto(request);
        requestDto.url = resolveDestinationURL(pattern, targetURLTemplate, requestDto.url);

        final HttpClient client = HttpClient.newHttpClient();
        final HttpResponse.BodyHandler<byte[]> bodyHandler = HttpResponse.BodyHandlers.ofByteArray();
        try {
            HttpRequest targetHttpReq = HttpRequestMapper.mapHttpRequestDtoToHttpRequest(requestDto);
            log.info("Calling URL: " + targetHttpReq.uri().toString());
            HttpResponse<byte[]> httpResponse = client.send(targetHttpReq, bodyHandler);

            httpResponse.headers().map().forEach((headerName, headerValueList) -> {
                String headerValue = headerValueList.stream().collect(Collectors.joining(", "));
                log.info("httpResponse to res: " + headerName + ": " + headerValue);

                if (HeaderUtils.isHeaderRestricted(headerName)) {
                    return;
                }
                // TODO: encode headervalues containing octet strtings
                response.addHeader(headerName, headerValue);
            });

            byte[] body = httpResponse.body();

            String charset = detectBodyCharacterEncoding(httpResponse);

            
            /**
             * TODO: Possible Feature: If the origin client send an accept or accept-charset
             * header check if the detected charset is accepted. when not, convert to one
             * the origin client accepts.
             */
            response.setCharacterEncoding(charset);
            response.getOutputStream().write(body);

            int status = response.getStatus();
            if (body.length > 0 && isSuccessfulStatusCode(status) == false) {
                response.setStatus(HttpServletResponse.SC_OK); // TODO: 200? or 207 or ?
            }

        } catch (InterruptedException | URISyntaxException e) { // TODO: rethink error handling
            e.printStackTrace();
        }

        // response.sendError(404, "application error"); // TODO error codes
    }

    private String detectBodyCharacterEncoding(HttpResponse<byte[]> httpResponse) {
        return null;
    }

    private boolean isSuccessfulStatusCode(int status) {
        return 200 <= status && status <= 299;
    }

}
