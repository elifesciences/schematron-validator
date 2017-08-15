package org.elifesciences.schematron.model;

public class NoSuchSchemaException extends Exception {
	private String schemaId;

	public String getSchemaId() {
		return schemaId;
	}

}
