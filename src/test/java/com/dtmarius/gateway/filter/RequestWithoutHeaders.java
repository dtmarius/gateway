package com.dtmarius.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class RequestWithoutHeaders {

	final HttpServletRequest request = mock(HttpServletRequest.class);
	final HttpServletResponse response = mock(HttpServletResponse.class);
	final FilterChain chain = mock(FilterChain.class);

	@BeforeEach
	public void setUp() {
		when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
	}

	@Test
	public void setHeader() throws IOException, ServletException {

		// arrange
		final String headerName = "MyCustomHeader";
		final String headerValue = "HeaderTestValue";
		final boolean overwriteExistingHeader = false;

		final UpstreamSetHeaderFilter filter = //
				new UpstreamSetHeaderFilter(headerName, headerValue, overwriteExistingHeader);

		when(request.getHeaders(headerName)).thenReturn(Collections.emptyEnumeration());

		// act
		filter.doFilter(request, response, chain);

		// assert
		ArgumentCaptor<MutableHttpServletRequest> argCaptor = //
				ArgumentCaptor.forClass(MutableHttpServletRequest.class);
		verify(chain).doFilter(argCaptor.capture(), any(HttpServletResponse.class));

		MutableHttpServletRequest mutatedRequest = argCaptor.getValue();
		assertThat(mutatedRequest.getHeader(headerName)).isEqualTo(headerValue);

		// in short:
		// verify(chain).doFilter(argThat(mutableRequest -> ((MutableHttpServletRequest)
		// mutableRequest)
		// .getHeader(headerName).equals(headerValue)), any(HttpServletResponse.class));
	}

	@Test
	public void removeHeader_ExpectedNoExceptions() throws IOException, ServletException {
		final String headerNameRegex = "X-.*";
		final UpstreamRemoveHeaderFilter filter = new UpstreamRemoveHeaderFilter(headerNameRegex);
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(MutableHttpServletRequest.class), any(HttpServletResponse.class));
	}

	@Test
	public void rewriteHeader_ExpectedNoExceptions() throws IOException, ServletException {
		String headerName = "ExampleHeader";
		String headerValueRegex = "^(?<value>.*)";
		String headerValueTemplate = "*${value}*";
		final UpstreamRewriteHeaderValueFilter filter = //
				new UpstreamRewriteHeaderValueFilter(headerName, headerValueRegex, headerValueTemplate);
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(request, response);
	}

}