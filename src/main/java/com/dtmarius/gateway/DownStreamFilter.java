/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author dtmarius.com
 */
public class DownstreamFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if((response instanceof HttpServletResponse) == false){
           // TODO: error handling evtl. exception???
        //    return;
        }

        // HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(httpServletResponse);
        // wrapper.setHeader(name, value);

        CustomHttpServletResponse customHttpServletResponse = new CustomHttpServletResponse((HttpServletResponse)response);
        customHttpServletResponse.setHeader("X-Marius", "Hello World");
        // customHttpServletResponse.addCookie(cookie); // evtl muss man die methoden gar nicht überschreiben um das zeugs zu setzen =.0
        response = customHttpServletResponse;
        
        chain.doFilter(request, response);
    }
}
