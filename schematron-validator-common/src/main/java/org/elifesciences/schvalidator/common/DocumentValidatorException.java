package org.elifesciences.schvalidator.common;

public final class DocumentValidatorException extends Exception {
    public DocumentValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentValidatorException(String message) {
        super(message);
    }
}
