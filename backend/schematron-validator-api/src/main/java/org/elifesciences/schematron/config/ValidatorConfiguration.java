package org.elifesciences.schematron.config;

import org.elifesciences.schematron.model.DocumentSchemaCollection;
import org.elifesciences.schematron.model.DocumentSchema;
import org.elifesciences.schvalidator.common.DocumentValidator;
import org.elifesciences.schvalidator.common.xslt2.Xslt2DocumentValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the {@link DocumentValidator}.  Contains the list of {@code schematron} schemas to be loaded
 * and registered with the validator.
 */
@Configuration
@ConfigurationProperties("validator")
public class ValidatorConfiguration {

	/**
	 * The list of schemas made available to the validator.
	 */
	private List<DocumentSchema> schemas = new ArrayList<>();

	public List<DocumentSchema> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<DocumentSchema> schemas) {
		this.schemas = schemas;
	}

	@Bean
	public DocumentSchemaCollection schemaCollection() {
		return new DocumentSchemaCollection(schemas);
	}

	@Bean
	public DocumentValidator validator() {
		return new Xslt2DocumentValidator();
	}
}
