package org.elifesciences.schvalidator.common;

public class InvalidSchemaException extends Exception {
    public InvalidSchemaException(String schemaId) {
        super(schemaId);
    }
}
