package com.dtmarius.gateway.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A UpstreamSetHeaderFilter is used to set a configured header before the
 * incoming request get processed by a HttpServlet.
 * 
 * The default value for the flag overwriteExistingHeader is true.
 * 
 * configuration example:
 * 
 * <pre>
 * {@code 
    <filter>
        <filter-name>upstreamSetHeaderFilter-MyCustomHeader</filter-name>
        <filter-class>com.dtmarius.gateway.filter.UpstreamSetHeaderFilter</filter-class>
        <init-param>
            <param-name>headerName</param-name>
            <param-value>MyCustomHeader</param-value>
        </init-param>
        <init-param>
            <param-name>headerValue</param-name>
            <param-value>Hello World</param-value>
        </init-param>
        <init-param>
            <param-name>overwriteExistingHeader</param-name>
            <param-value>false</param-value>
        </init-param>
    </filter>
   }
 * </pre>
 */
public class UpstreamSetHeaderFilter implements Filter {

    private static Logger log = Logger.getLogger(UpstreamSetHeaderFilter.class.getName());

    private String headerName;
    private String headerValue;
    private boolean overwriteExistingHeader = true;

    UpstreamSetHeaderFilter(String headerName, String headerValue, boolean overwriteExistingHeader) {
        this.headerName = headerName;
        this.headerValue = headerValue;
        this.overwriteExistingHeader = overwriteExistingHeader;
        log.info("activated " + this.toString());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.headerName = filterConfig.getInitParameter("headerName");
        this.headerValue = filterConfig.getInitParameter("headerValue");
        String overwriteExistingHeaderString = filterConfig.getInitParameter("overwriteExistingHeader");
        this.overwriteExistingHeader = Boolean.valueOf(overwriteExistingHeaderString);
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
        chain.doFilter(request, response);
    }

    void process(HttpServletRequest request) {
        if (request.getHeader(headerName) != null && overwriteExistingHeader == false)
            return;

        ModifyableHttpServletRequest httpRequest = new ModifyableHttpServletRequest(request);
        request = httpRequest;
        httpRequest.setHeader(this.headerName, this.headerValue);
    }

    private boolean notInstanceOfHttpServlet(ServletRequest request) {
        return !(request instanceof HttpServletRequest);
    }

    @Override
    public String toString() {
        return String.format("UpstreamSetHeaderFilter[headerName=%s, headerValue=%s, overwriteExistingHeader=%s]",
                headerName, headerValue, overwriteExistingHeader);
    }

}