package org.elifesciences.schvalidator.common.puresch.jaxp;

import com.google.common.collect.ImmutableList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.schematron.SchematronException;
import com.helger.schematron.pure.binding.IPSQueryBinding;
import com.helger.schematron.pure.binding.PSQueryBindingRegistry;
import com.helger.schematron.pure.bound.IPSBoundSchema;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.errorhandler.LoggingPSErrorHandler;
import com.helger.schematron.pure.exchange.PSReader;
import com.helger.schematron.pure.model.PSSchema;
import com.helger.schematron.pure.preprocess.PSPreprocessor;
import com.helger.schematron.pure.validation.SchematronValidationException;
import com.helger.schematron.pure.validation.xpath.PSXPathValidationHandlerSVRL;
import org.elifesciences.schvalidator.common.*;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JaxpDocumentValidator implements DocumentValidator {

	private final Map<String, IPSBoundSchema> schemaMap = new HashMap<>();

	@Override
	public void registerSchema(String schemaName, String source, List<String> transformerPaths)
		throws DocumentValidatorException, InvalidSchemaException {
		CollectingPSErrorHandler errorHandler = new CollectingPSErrorHandler();

		try {
			PSReader schemaReader = new PSReader(new ClassPathResource(source));
			PSSchema schema = schemaReader.readSchema();

			if (!schema.isValid(errorHandler)) {
				throw new InvalidSchemaException("Invalid schema.");
			}

			String bindingName = schema.getQueryBinding();
			IPSQueryBinding queryBinding = PSQueryBindingRegistry.getQueryBindingOfNameOrThrow(bindingName);

			PSPreprocessor preprocessor = new PSPreprocessor(queryBinding)
				.setKeepDiagnostics(true)
				.setKeepReports(true)
				.setKeepTitles(true)
				.setKeepEmptyPatterns(true)
				.setKeepEmptySchema(true);

			PSSchema processedSchema = preprocessor.getForcedPreprocessedSchema(schema);
			if (processedSchema == null) {
				throw new DocumentValidatorException("Unable to preprocess schema");
			}

			IPSBoundSchema boundSchema = queryBinding.bind(processedSchema, null, errorHandler, null, null);
			schemaMap.put(schemaName, boundSchema);
		} catch (SchematronException e) {
			throw new DocumentValidatorException("Unable to load schema", e);
		}
	}

	public List<Diagnostic> validate(Document document, String schemaName) throws DocumentValidatorException {
		IPSBoundSchema boundSchema = schemaMap.get(schemaName);
		if (boundSchema == null) {
			throw new DocumentValidatorException("No schema named " + schemaName + " is registered with this validator");
		}

		CollectingPSErrorHandler errors = new CollectingPSErrorHandler(new LoggingPSErrorHandler());
		PSXPathValidationHandlerSVRL validationHandler = new PSXPathValidationHandlerSVRL(errors);

		try {
			boundSchema.validate(document, validationHandler);

			if (!errors.isEmpty()) {
				throw new DocumentValidatorException("Error occurred evaluating schema");
			}
		} catch (SchematronValidationException ex) {
			throw new DocumentValidatorException("Unable to validate document", ex);
		}

		SchematronOutputType output = validationHandler.getSVRL();
		if (output.hasNoTextEntries()) {
			return ImmutableList.of();
		}

		return output.getText().stream()
			.map(text -> new Diagnostic("test", text, DiagnosticLevel.ERROR))
			.collect(Collectors.toList());
	}
}
