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
 * A UpstreamRewriteHeaderValueFilter is used to rewrite all header values for a
 * header name to the configured template before the incoming request get
 * processed by a HttpServlet.
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
            <param-value>^(?&lt;value&gt;.*)$</param-value>
        </init-param>
        <init-param>
            <param-name>headerValueTemplate</param-name>
            <param-value>*${value}*</param-value>
        </init-param>
    </filter>
   }
 * </pre>
 */
public class UpstreamRewriteHeaderValueFilter extends HttpFilter {

    private static final long serialVersionUID = 6941983021246085864L;

    private static Logger log = Logger.getLogger(UpstreamRemoveHeaderFilter.class.getName());

    private String headerName;
    private String headerValueRegex;
    private String headerValueTemplate;

    private Pattern headerValueRegexPattern;

    public UpstreamRewriteHeaderValueFilter() {
    }

    UpstreamRewriteHeaderValueFilter(String headerName, String headerValueRegex, String headerValueTemplate) {
        initialize(headerName, headerValueRegex, headerValueTemplate);
    }

    @Override
    public void init() throws ServletException {
        FilterConfig filterConfig = getFilterConfig();
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
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request.getHeader(headerName) == null) {
            chain.doFilter(request, response);
            return;
        }

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.rewriteHeaderValuesMatchingPattern(headerName, headerValueRegexPattern, headerValueTemplate);

        chain.doFilter(mutableRequest, response);
    }

    @Override
    public String toString() {
        return String.format(
                "UpstreamRewriteHeaderValueFilter[headerName=%s, headerValueRegex=%s, headerValueTemplate=%s]",
                headerName, headerValueRegex, headerValueTemplate);
    }

}