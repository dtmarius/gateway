/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

    @Inject
    private HttpTranslator translator;

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

        final HttpClient client = HttpClient.newHttpClient();
        final HttpResponse.BodyHandler<byte[]> res = HttpResponse.BodyHandlers.ofByteArray();
        try {
            HttpResponse<byte[]> httpResponse = client.send(incomingHttpRequest.toHttpRequest(), res);
            // TODO: send httpResponse back
        } catch (InterruptedException | URISyntaxException e) { // TODO: rethink error handling
            e.printStackTrace();
        }

        // response.sendError(404, "application error"); // TODO error codes
    }

    boolean isHeaderRestricted(String headerName) {
        final String[] restrictedHeaders = new String[] { "HOST", "CONNECTION", "CONTENT-LENGTH" };
        for (String restrictedHeader : restrictedHeaders) {
            if (restrictedHeader.equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }

}
