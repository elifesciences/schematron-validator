package org.elifesciences.schematron.model;

import java.util.ArrayList;
import java.util.List;

public final class DocumentSchema {

	/**
	 * Unique id of this schema.
	 */
	private String id;

	/**
	 * The absolute path to this schema on the classpath.
	 */
	private String path;

	/**
	 * A list of XSLT stylesheets applied to this schema before it is processed.
	 */
	private List<String> transforms = new ArrayList<>();

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

	public List<String> getTransforms() {
		return transforms;
	}

	public void setTransforms(List<String> transforms) {
		this.transforms = transforms;
	}
}
