package com.dtmarius.gateway;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class HeaderUtils {

    private static final Set<String> RESTRICTED_HEADERS_SET;

    static {
        TreeSet<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        treeSet.addAll(Set.of("connection", "content-length", "date", "expect",
                "from", "host", "upgrade", "via", "warning"));
        RESTRICTED_HEADERS_SET = Collections.unmodifiableSet(treeSet);
    }

    public static boolean isHeaderRestricted(String headerName) {
        return RESTRICTED_HEADERS_SET.contains(headerName.toLowerCase());
    }

}