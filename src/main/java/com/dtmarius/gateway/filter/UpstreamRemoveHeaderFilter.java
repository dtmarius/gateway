package com.dtmarius.gateway.filter;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class UpstreamRemoveHeaderFilter extends HttpFilter {

    private static final long serialVersionUID = -8991376492972140334L;

    private static Logger log = Logger.getLogger(UpstreamRemoveHeaderFilter.class.getName());

    private String headerNameRegex;

    private Pattern headerNameRegexPattern;

    public UpstreamRemoveHeaderFilter() {
    }

    UpstreamRemoveHeaderFilter(String headerNameRegex) {
        initialize(headerNameRegex);
    }

    @Override
    public void init() throws ServletException {
        FilterConfig filterConfig = getFilterConfig();
        String headerNameRegex = filterConfig.getInitParameter("headerNameRegex");
        initialize(headerNameRegex);
    }

    private void initialize(String headerNameRegex) {
        this.headerNameRegex = headerNameRegex;
        this.headerNameRegexPattern = Pattern.compile(headerNameRegex);
        log.info("activated " + this.toString());
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.removeHeadersMatchingPattern(headerNameRegexPattern);

        chain.doFilter(mutableRequest, response);
    }

    @Override
    public String toString() {
        return String.format("UpstreamRemoveHeaderFilter[headerNameRegex=%s]", headerNameRegex);
    }

}