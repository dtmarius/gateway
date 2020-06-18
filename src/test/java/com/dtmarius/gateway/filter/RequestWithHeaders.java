package com.dtmarius.gateway.filter;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class RequestWithHeaders {

	final HttpServletRequest request = mock(HttpServletRequest.class);
	final HttpServletResponse response = mock(HttpServletResponse.class);
	final FilterChain chain = mock(FilterChain.class);

	@BeforeEach
	public void setUp() {
		final HashMap<String, List<String>> headerMap = new HashMap<>();
		headerMap.put("Accept-Encoding", asList("gzip", "deflate", "br"));
		headerMap.put("Accept-Language", asList("de-DE", "en-US"));
		headerMap.put("Connection", asList("keep-alive"));
		headerMap.put("X-MyHeader", asList("testValue"));

		when(request.getHeaderNames()).thenReturn(Collections.enumeration(headerMap.keySet()));

		when(request.getHeader(anyString())).then(answer -> {
			String headerName = answer.getArgument(0, String.class);
			List<String> headerValues = headerMap.get(headerName);
			if (headerValues == null || headerValues.size() <= 0)
				return null;
			else
				return headerValues.get(0);
		});

		when(request.getHeaders(anyString())).then(answer -> {
			String headerName = answer.getArgument(0, String.class);
			if (headerName == null || headerMap.get(headerName) == null || headerMap.get(headerName).size() <= 0)
				return Collections.emptyEnumeration();
			return Collections.enumeration(headerMap.get(headerName));
		});
	}

	@Test
	public void addNewHeader() throws IOException, ServletException {

		// arrange
		final String headerName = "X-CustomHeader";
		final String headerValue = "testValue";
		final boolean overwriteExistingHeader = false;

		final UpstreamSetHeaderFilter filter = //
				new UpstreamSetHeaderFilter(headerName, headerValue, overwriteExistingHeader);

		when(request.getHeaders(headerName)).thenReturn(Collections.emptyEnumeration());

		// act
		filter.doFilter(request, response, chain);

		// assert
		final ArgumentCaptor<MutableHttpServletRequest> argCaptor = //
				ArgumentCaptor.forClass(MutableHttpServletRequest.class);
		verify(chain).doFilter(argCaptor.capture(), any(HttpServletResponse.class));

		final MutableHttpServletRequest mutatedRequest = argCaptor.getValue();
		assertThat(mutatedRequest.getHeader(headerName)).isEqualTo(headerValue);
	}

	@Test
	public void doNotOverrideExistingHeader() throws IOException, ServletException {

		// arrange
		final String headerName = "X-MyHeader";
		final String headerValue = "myOverriddenValue";
		final boolean overwriteExistingHeader = false;

		final UpstreamSetHeaderFilter filter = //
				new UpstreamSetHeaderFilter(headerName, headerValue, overwriteExistingHeader);

		when(request.getHeaders(headerName)).thenReturn(Collections.emptyEnumeration());

		// act
		filter.doFilter(request, response, chain);

		// assert
		final ArgumentCaptor<HttpServletRequest> argCaptor = //
				ArgumentCaptor.forClass(HttpServletRequest.class);
		verify(chain).doFilter(argCaptor.capture(), any(HttpServletResponse.class));

		final HttpServletRequest request = argCaptor.getValue();
		assertThat(request.getHeader(headerName)).isNotEqualTo(headerValue);
	}

	@Test
	public void overrideExistingHeader() throws IOException, ServletException {

		// arrange
		final String headerName = "X-MyHeader";
		final String headerValue = "newTestValue";
		final boolean overwriteExistingHeader = true;

		final UpstreamSetHeaderFilter filter = //
				new UpstreamSetHeaderFilter(headerName, headerValue, overwriteExistingHeader);

		when(request.getHeaders(headerName)).thenReturn(Collections.emptyEnumeration());

		// act
		filter.doFilter(request, response, chain);

		// assert
		final ArgumentCaptor<MutableHttpServletRequest> argCaptor = //
				ArgumentCaptor.forClass(MutableHttpServletRequest.class);
		verify(chain).doFilter(argCaptor.capture(), any(HttpServletResponse.class));

		final MutableHttpServletRequest mutatedRequest = argCaptor.getValue();
		assertThat(mutatedRequest.getHeader(headerName)).isEqualTo(headerValue);

	}

	@Test
	public void removeHeader() throws IOException, ServletException {
		final String headerNameRegex = "Accept-.*";
		final UpstreamRemoveHeaderFilter filter = new UpstreamRemoveHeaderFilter(headerNameRegex);
		filter.doFilter(request, response, chain);

		ArgumentCaptor<MutableHttpServletRequest> argCaptor = //
				ArgumentCaptor.forClass(MutableHttpServletRequest.class);

		verify(chain).doFilter(argCaptor.capture(), any(HttpServletResponse.class));

		final MutableHttpServletRequest mutatedRequest = argCaptor.getValue();

		assertThat(Collections.list(mutatedRequest.getHeaderNames())).doesNotContain("Accept-Encoding");
		assertThat(Collections.list(mutatedRequest.getHeaderNames())).doesNotContain("Accept-Language");
	}

}