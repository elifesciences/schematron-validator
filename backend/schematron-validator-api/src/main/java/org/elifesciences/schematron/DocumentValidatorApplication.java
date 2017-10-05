package org.elifesciences.schematron;

import org.elifesciences.schematron.model.DocumentSchema;
import org.elifesciences.schematron.model.DocumentSchemaCollection;
import org.elifesciences.schvalidator.common.DocumentValidator;
import org.elifesciences.schvalidator.common.DocumentValidatorException;
import org.elifesciences.schvalidator.common.InvalidSchemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Collections;

@SpringBootApplication
public class DocumentValidatorApplication {

	private final DocumentValidator validator;
	private final DocumentSchemaCollection schemas;

	@Autowired
	public DocumentValidatorApplication(DocumentValidator validator, DocumentSchemaCollection schemas) {
		this.validator = validator;
		this.schemas = schemas;
	}

	public static void main(String[] argv) {
		SpringApplication.run(DocumentValidatorApplication.class, argv);
	}

	@PostConstruct
	public void registerSchemas() throws DocumentValidatorException, InvalidSchemaException {
		for (DocumentSchema schema : schemas) {
			validator.registerSchema(schema.getId(), schema.getPath(), schema.getTransforms());
		}
	}
}
