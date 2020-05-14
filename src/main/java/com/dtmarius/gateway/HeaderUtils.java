package com.dtmarius.gateway;

import java.util.Set;

public class HeaderUtils {

    private static Set<String> restrictedHeaders = Set.of("connection", "content-length", "expect", "host", "upgrade");

    public static boolean isHeaderRestricted(String headerName) {
        return restrictedHeaders.contains(headerName.toLowerCase());
    }

}