package com.dtmarius.gateway.utils;

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
