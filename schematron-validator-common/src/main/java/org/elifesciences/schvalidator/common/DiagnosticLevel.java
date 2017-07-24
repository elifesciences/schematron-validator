package org.elifesciences.schvalidator.common;

public enum DiagnosticLevel {
    INFO,
    WARNING,
    ERROR;

	public static DiagnosticLevel from(String value) {
		switch (value.toLowerCase()) {
			case "warning":
				return WARNING;
			case "error":
				return ERROR;
			case "info":
				return INFO;
			default:
				throw new IllegalArgumentException("Invalid diagnostic level");
		}
	}
}
