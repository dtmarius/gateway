package com.dtmarius.gateway.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A UpstreamRewriteHeaderValueFilter is used to rewrite the header value to the
 * configured template before the incoming request get processed by a
 * HttpServlet.
 * 
 * configuration example:
 * 
 * <pre>
 * {@code 
    <filter>
        <filter-name>upstreamRewriteHeaderValueFilter-ExampleHeader</filter-name>
        <filter-class>com.dtmarius.gateway.filter.UpstreamRewriteHeaderValueFilter</filter-class>
        <init-param>
            <param-name>headerName</param-name>
            <param-value>ExampleHeader</param-value>
        </init-param>
        <init-param>
            <param-name>headerValueRegex</param-name>
            <param-value>^(?&lt;value&gt;.*)</param-value>
        </init-param>
        <init-param>
            <param-name>headerValueTemplate</param-name>
            <param-value>*${value}*</param-value>
        </init-param>
    </filter>
   }
 * </pre>
 */
public class UpstreamRewriteHeaderValueFilter implements Filter {

    private static Logger log = Logger.getLogger(UpstreamRemoveHeaderFilter.class.getName());

    private String headerName;
    private String headerValueRegex;
    private String headerValueTemplate;

    private Pattern headerValueRegexPattern;

    UpstreamRewriteHeaderValueFilter(String headerName, String headerValueRegex, String headerValueTemplate) {
        initialize(headerName, headerValueRegex, headerValueTemplate);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String headerName = filterConfig.getInitParameter("headerName");
        String headerValueRegex = filterConfig.getInitParameter("headerValueRegex");
        String headerValueTemplate = filterConfig.getInitParameter("headerValueTemplate");
        initialize(headerName, headerValueRegex, headerValueTemplate);
    }

    private void initialize(String headerName, String headerValueRegex, String headerValueTemplate) {
        this.headerName = headerName;
        this.headerValueRegex = headerValueRegex;
        this.headerValueTemplate = headerValueTemplate;

        this.headerValueRegexPattern = Pattern.compile(headerValueRegex);

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

        String headerValue = request.getHeader(headerName);
        if (headerValue == null) {
            return;
        }

        Matcher matcher = headerValueRegexPattern.matcher(headerValue);
        String processedHeaderValue = matcher.replaceAll(headerValueTemplate);

        httpRequest.setHeader(headerName, processedHeaderValue);
    }

    private boolean notInstanceOfHttpServlet(ServletRequest request) {
        return !(request instanceof HttpServletRequest);
    }

    @Override
    public String toString() {
        return String.format(
                "UpstreamRewriteHeaderValueFilter[headerName=%s, headerValueRegex=%s, headerValueTemplate=%s]",
                headerName, headerValueRegex, headerValueTemplate);
    }

}