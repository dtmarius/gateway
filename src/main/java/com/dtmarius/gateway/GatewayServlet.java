/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dtmarius.com
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

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        IncomingHttpRequest incomingHttpRequest = IncomingHttpRequest.ofHttpServletRequest(request);
        incomingHttpRequest.resolveTargetURL(pattern, targetURLTemplate);

        final HttpClient client = HttpClient.newHttpClient();
        final HttpResponse.BodyHandler<byte[]> res = HttpResponse.BodyHandlers.ofByteArray();
        try {
            HttpRequest httpReq = incomingHttpRequest.toHttpRequest();
            log.info("Calling URL: " + httpReq.uri().toString());
            HttpResponse<byte[]> httpResponse = client.send(httpReq, res);

            httpResponse.headers().map().forEach((headerName, headerValueList) -> {
                String headerValue = headerValueList.stream().collect(Collectors.joining(", "));
                log.info("httpResponse to res: " + headerName + ": " + headerValue);

                if (HeaderUtils.isHeaderRestricted(headerName)) {
                    return;

                }
                response.addHeader(headerName, headerValue);
            });

            byte[] body = httpResponse.body();
            response.getOutputStream().write(body);

        } catch (InterruptedException | URISyntaxException e) { // TODO: rethink error handling
            e.printStackTrace();
        }

        // response.sendError(404, "application error"); // TODO error codes
    }

}
