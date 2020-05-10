/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author dtmarius.com
 */
public class GatewayServletIT {

    @Test
    public void demo() throws ServletException, IOException {
        GatewayServlet g = new GatewayServlet();
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameter("name")).thenReturn("Marius");
        g.init(config);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        
        
        StringWriter writer = new StringWriter();
        when(res.getWriter()).thenReturn(new PrintWriter(writer));
        g.service(req, res);
        //assertThat(res.getHeader("test")).isEqualTo("hallo Marius");
    }
}
