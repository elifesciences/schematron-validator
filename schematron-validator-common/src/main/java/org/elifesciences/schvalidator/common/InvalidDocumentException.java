package org.elifesciences.schvalidator.common;

public class InvalidDocumentException extends Exception {
	public InvalidDocumentException(String message, Throwable e) {
		super(message, e);
	}
}
