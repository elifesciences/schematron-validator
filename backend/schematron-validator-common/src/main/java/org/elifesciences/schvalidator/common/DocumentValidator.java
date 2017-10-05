package org.elifesciences.schvalidator.common;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A validator that processes XML {@link Document}s against s and returns a list of {@link Diagnostic}s.
 */
public interface DocumentValidator {

	String DOCUMENT_LOAD_EXTERNAL_DTDS = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/**
	 * Register a schema with this validator.
	 * @param schemaName The name to associate with this schema.
	 * @param inputPath The source to load the schema from.
	 * @param transformerPaths
	 */
	void registerSchema(String schemaName, String inputPath, List<String> transformerPaths) throws InvalidSchemaException, DocumentValidatorException;

	/**
	 * Validate an XML document against a schema with the given {@code schemaName}.
	 *
	 * @param document The XML document to validate.
	 * @param schema The schema the XML document is to be validated against.
	 * @return A {@link List} of errors encountered while parsing the document.
	 * @throws DocumentValidatorException If an internal error occurred during validation.
	 * @throws InvalidDocumentException   If the supplied document was detected as invalid XML.
	 */
	default List<Diagnostic> validate(InputStream document, String schema)
		throws DocumentValidatorException,
			   InvalidDocumentException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(DOCUMENT_LOAD_EXTERNAL_DTDS, false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(document));

			return validate(doc, schema);
		} catch (ParserConfigurationException | IOException ex) {
			throw new DocumentValidatorException("Error initializing XML parser", ex);
		} catch (SAXException e) {
			throw new InvalidDocumentException("Unable to parse XML document", e);
		}
	}

	List<Diagnostic> validate(Document document, String schema) throws DocumentValidatorException;
}
