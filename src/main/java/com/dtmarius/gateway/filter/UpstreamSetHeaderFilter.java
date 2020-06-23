package com.dtmarius.gateway.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class UpstreamSetHeaderFilter extends HttpFilter {

    private static final long serialVersionUID = -3010583173466063766L;

    private static Logger log = Logger.getLogger(UpstreamSetHeaderFilter.class.getName());

    private String headerName;
    private String headerValue;
    private boolean overwriteExistingHeader = true;

    public UpstreamSetHeaderFilter() {
    }

    UpstreamSetHeaderFilter(String headerName, String headerValue, boolean overwriteExistingHeader) {
        this.headerName = headerName;
        this.headerValue = headerValue;
        this.overwriteExistingHeader = overwriteExistingHeader;
        log.info("activated " + this.toString());
    }

    @Override
    public void init() throws ServletException {
        FilterConfig filterConfig = getFilterConfig();
        this.headerName = filterConfig.getInitParameter("headerName");
        this.headerValue = filterConfig.getInitParameter("headerValue");
        String overwriteExistingHeaderString = filterConfig.getInitParameter("overwriteExistingHeader");
        this.overwriteExistingHeader = Boolean.valueOf(overwriteExistingHeaderString);
        log.info("activated " + this.toString());
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request.getHeader(headerName) != null && overwriteExistingHeader == false) {
            chain.doFilter(request, response);
            return;
        }

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.setHeader(this.headerName, this.headerValue);

        chain.doFilter(mutableRequest, response);
    }

    @Override
    public String toString() {
        return String.format("UpstreamSetHeaderFilter[headerName=%s, headerValue=%s, overwriteExistingHeader=%s]",
                headerName, headerValue, overwriteExistingHeader);
    }

}