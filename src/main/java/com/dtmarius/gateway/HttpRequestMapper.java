import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.faces.annotation.HeaderMap;
import javax.servlet.http.HttpServletRequest;

public class HttpRequestMapper {

    private static String getFullRequestURL(HttpServletRequest request) {
        StringBuffer fullRequestURLString = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            fullRequestURLString.append('?').append(queryString);
        }
        return fullRequestURLString.toString();
    }

    private static HashMap<String, ArrayList<String>> getHeaderAsMap(HttpServletRequest request) {
        final HashMap<String, ArrayList<String>> headerMap = new HashMap<>();
        for (final Iterator<String> it = request.getHeaderNames().asIterator(); it.hasNext();) {
            final String headerName = it.next();
            final ArrayList<String> headerValues = Collections.list(request.getHeaders(headerName));
            headerMap.put(headerName, headerValues);
        }
        return headerMap;
    }

    public static HttpRequestDto mapHttpServletRequestToHttpRequestDto(HttpServletRequest request) {
        HttpRequestDto dto = new HttpRequestDto();
        dto.method = request.getMethod();
        dto.url = getFullRequestURL(request);
        dto.headerMap = getHeaderAsMap(request);
        dto.body = request.getInputStream().readAllBytes();
        return dto;
    }

    public static HttpRequest mapHttpRequestDtoToHttpRequest(HttpRequestDto requestDto) {
        final ArrayList<String> headerList = new ArrayList<>();
        for (final Entry<String, ArrayList<String>> entry : requestDto.headerMap.entrySet()) {
            final String headerName = entry.getKey();
            if (HeaderUtils.isHeaderRestricted(headerName)) {
                continue;
            }
            final String headerValue = entry.getValue().stream().collect(Collectors.joining(", "));
            headerList.add(headerName);
            headerList.add(headerValue);
        }

        final String[] headers = headerList.toArray(new String[headerList.size()]);

        final URI uri = URI.create(requestDto.url);

        return HttpRequest.newBuilder()//
                .version(Version.HTTP_1_1)//
                .uri(uri)//
                .headers(headers)//
                .method(requestDto.method, HttpRequest.BodyPublishers.ofByteArray(requestDto.body))//
                .build();
    }
}