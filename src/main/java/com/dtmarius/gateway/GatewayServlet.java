/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

/**
 *
 * @author dtmarius.com
 */
public class GatewayServlet extends HttpServlet {

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
        log.info("requestURLRegex:_" + requestURLRegex);
        log.info("targetURL:_" + targetURLTemplate);
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        log.info(request.getRequestURL() + "_");

        IncomingHttpRequest incomingHttpRequest = IncomingHttpRequest.ofHttpServletRequest(request);

        // move resolveTargetURL out of here
        final String targetURL = resolveTargetURL(request);
        log.info("targetURL: " + targetURL);

        final HttpClient client = HttpClient.newHttpClient();
        final BodyHandler<InputStream> res = BodyHandlers.ofInputStream();
        HttpRequest targetRequests = buildTargetRequests(request, targetURL);
        try {
            client.send(targetRequests, res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // System.out.println("not found");
        // response.sendError(404, "application error"); // TODO error codes
        // ueberarbeiten
    }

    HttpRequest buildTargetRequests(final HttpServletRequest servletRequest, final String targetURL) {

        return translator.translateHtppServletRequestToHttpRequest(servletRequest);
    }

    String resolveTargetURL(final HttpServletRequest request) {
        final Matcher matcher = pattern.matcher(request.getRequestURL());
        if (matcher.find() == false) {
            return null; // TODO: overthink errorhandling
        }

        log.info("t:" + targetURLTemplate);
        final String resolvedTargetURL = matcher.replaceAll(targetURLTemplate);
        log.info("rt:" + resolvedTargetURL);
        return resolvedTargetURL;
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
