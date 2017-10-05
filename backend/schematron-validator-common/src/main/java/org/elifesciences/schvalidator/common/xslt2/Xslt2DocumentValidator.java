package org.elifesciences.schvalidator.common.xslt2;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.xslt.SchematronResourceSCH;
import org.elifesciences.schvalidator.common.*;
import org.elifesciences.schvalidator.common.puresch.jaxp.XsltTransformableSchematronResource;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.oclc.purl.dsdl.svrl.SuccessfulReport;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Xslt2DocumentValidator implements DocumentValidator {
	private final Map<String, ISchematronResource> schemaMap = new HashMap<>();

	@Override
	public void registerSchema(String schemaName, String inputPath, List<String> transformerPaths)
		throws InvalidSchemaException, DocumentValidatorException {

		final List<Transformer> transformerList = new ArrayList<>();
		final TransformerFactory factory = TransformerFactory.newInstance();

		for (String transformerPath : transformerPaths) {
			URL xsltURL = this.getClass().getResource(transformerPath);
			try {
				Source xslt = new StreamSource(xsltURL.openStream(), xsltURL.toExternalForm());
				Transformer transformer = factory.newTransformer(xslt);
				transformerList.add(transformer);
			} catch (IOException  | TransformerConfigurationException e) {
				throw new InvalidSchemaException("Failed to create transformers", e);
			}
		}

		final ClassPathResource resource = new ClassPathResource(inputPath);
		final SchematronResourceSCH schema = new XsltTransformableSchematronResource(resource, transformerList);

		if (!schema.isValidSchematron()) {
			throw new InvalidSchemaException(schemaName);
		}

		schemaMap.put(schemaName, schema);
	}

	@Override
	public List<Diagnostic> validate(Document document, String schemaName) throws DocumentValidatorException {
		ISchematronResource boundSchema = schemaMap.get(schemaName);
		if (boundSchema == null) {
			throw new DocumentValidatorException("No schema named " + schemaName + " is registered with this validator");
		}

		try {
			SchematronOutputType output = boundSchema.applySchematronValidationToSVRL(new DOMSource(document));
			if (output == null) {
				throw new DocumentValidatorException("No output from the document validator");
			}

			List<Object> assertions = output.getActivePatternAndFiredRuleAndFailedAssert();
			List<Diagnostic> diagnostics = new ArrayList<>(assertions.size());

			for (Object assertion : assertions) {
				if (assertion instanceof FailedAssert) {
					FailedAssert error = (FailedAssert) assertion;
					DiagnosticLevel level = DiagnosticLevel.valueOf(error.getRole().toUpperCase());
					diagnostics.add(new Diagnostic(error.getLocation(), error.getText(), level));
				} else if (assertion instanceof SuccessfulReport) {
					SuccessfulReport report = (SuccessfulReport) assertion;
					DiagnosticLevel level = DiagnosticLevel.valueOf(report.getRole().toUpperCase());
					diagnostics.add(new Diagnostic(report.getLocation(), report.getText(), level));
				}
			}

			return diagnostics;
		} catch (Exception ex) {
			throw new DocumentValidatorException("Unable to validate document", ex);
		}
	}
}
