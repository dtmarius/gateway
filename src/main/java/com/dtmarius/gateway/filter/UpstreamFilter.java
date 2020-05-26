/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * TODO: remove or use as abstract class
 * 
 * @author dtmarius.com
 */
public class UpstreamFilter implements Filter {

    // UpstreamSetHeaderFilter
    // UpstreamRemoveHeaderFilter
    // UpstreamRewriteHeaderValueFilter
    // UpstreamRewriteHeaderFilter
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}
