package com.dtmarius.gateway.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A UpstreamRemoveHeaderFilter is used to remove headers matching the
 * configured regex before the incoming request get processed by a HttpServlet.
 * 
 * configuration example:
 * 
 * <pre>
 * {@code 
    <filter>
        <filter-name>upstreamRemoveHeaderFilter-MyCustomHeader</filter-name>
        <filter-class>com.dtmarius.gateway.filter.UpstreamRemoveHeaderFilter</filter-class>
        <init-param>
            <param-name>headerNameRegex</param-name>
            <param-value>My.*Header</param-value>
        </init-param>
    </filter>
   }
 * </pre>
 */
public class UpstreamRemoveHeaderFilter implements Filter {

    private static Logger log = Logger.getLogger(UpstreamRemoveHeaderFilter.class.getName());

    private String headerNameRegex;

    private Pattern headerNameRegexPattern;

    UpstreamRemoveHeaderFilter(String headerNameRegex) {
        initialize(headerNameRegex);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String headerNameRegex = filterConfig.getInitParameter("headerNameRegex");
        initialize(headerNameRegex);
    }

    private void initialize(String headerNameRegex) {
        this.headerNameRegex = headerNameRegex;
        this.headerNameRegexPattern = Pattern.compile(headerNameRegex);
        log.info("activated " + this.toString());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (notInstanceOfHttpServlet(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        process(httpServletRequest);
        chain.doFilter(httpServletRequest, response);
    }

    void process(HttpServletRequest request) {
        ModifyableHttpServletRequest httpRequest = new ModifyableHttpServletRequest(request);
        for (final Iterator<String> it = request.getHeaderNames().asIterator(); it.hasNext();) {
            final String headerName = it.next();

            if (headerNameRegexPattern.matcher(headerName).matches()) {
                httpRequest.removeHeader(headerName);
            }
        }
    }

    private boolean notInstanceOfHttpServlet(ServletRequest request) {
        return !(request instanceof HttpServletRequest);
    }

    @Override
    public String toString() {
        return String.format("UpstreamRemoveHeaderFilter[headerNameRegex=%s]", headerNameRegex);
    }

}