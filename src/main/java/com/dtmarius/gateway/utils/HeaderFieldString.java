package com.dtmarius.gateway.utils;

/**
 * Header fields are case insensitive. Some application servers convert the
 * header fields to lower case and some leave it as is. To be application server
 * independent this class is a case insensitive wrapper for header field
 * strings.
 *
 */
public class HeaderFieldString {

    private String headerField;

    public HeaderFieldString(String headerField) {
        this.headerField = headerField;
    }

    @Override
    public int hashCode() {
        return headerField.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof HeaderFieldString))
            return false;
        HeaderFieldString other = (HeaderFieldString) obj;
        return headerField.equalsIgnoreCase(other.headerField);

    }

    @Override
    public String toString() {
        return headerField.toString();
    }

}
