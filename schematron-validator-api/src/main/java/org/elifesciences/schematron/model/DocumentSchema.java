package org.elifesciences.schematron.model;

public final class DocumentSchema {

	/**
	 * Unique id of this schema.
	 */
	private String id;

	/**
	 * The absolute path to this schema on the classpath.
	 */
	private String path;

	public DocumentSchema() {

	}

	public DocumentSchema(String id, String path) {
		this.id = id;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
