package org.elifesciences.schematron.api;

import com.google.common.base.Joiner;
import org.elifesciences.schematron.model.DocumentSchema;
import org.elifesciences.schematron.model.DocumentSchemaCollection;
import org.elifesciences.schematron.model.DocumentValidationResult;
import org.elifesciences.schematron.model.NoSuchSchemaException;
import org.elifesciences.schvalidator.common.Diagnostic;
import org.elifesciences.schvalidator.common.DocumentValidator;
import org.elifesciences.schvalidator.common.DocumentValidatorException;
import org.elifesciences.schvalidator.common.InvalidDocumentException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@Controller
public final class DocumentValidationController {
	public static final String VALIDATE_FILE_PATH = "/document-validator/{schemaId}/file";

	private final DocumentSchemaCollection schemas;
	private final DocumentValidator validator;
	private final MessageSource messages;

	public DocumentValidationController(MessageSource messages,
										DocumentSchemaCollection schemas,
										DocumentValidator validator) {
		this.messages = messages;
		this.schemas = schemas;
		this.validator = validator;
	}

	@ExceptionHandler(NoSuchSchemaException.class)
	public ResponseEntity<String> handleNoSuchSchema(NoSuchSchemaException ex) {
		String schema = ex.getSchemaId();
		String schemaList = Joiner.on(", ").join(schemas);

		Locale locale = LocaleContextHolder.getLocale();
		Object[] errorMessageArgs = {schema, schemaList};
		String errorMessage = messages.getMessage("app.error.invalid_schema", errorMessageArgs, locale);

		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidDocumentException.class)
	public ResponseEntity<String> handleInvalidDocument() {
		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messages.getMessage("app.error.invalid_document", null, locale);

		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DocumentValidatorException.class)
	public ResponseEntity<String> handleValidationError() {
		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = messages.getMessage("app.error.internal", null, locale);

		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseBody
	@PostMapping(VALIDATE_FILE_PATH)
	public DocumentValidationResult validateFile(@PathVariable("schemaId") String schemaId,
												 @RequestParam("document") MultipartFile file) throws Exception {
		DocumentSchema schema = schemas.find(schemaId);
		List<Diagnostic> diagnostics = validator.validate(file.getInputStream(), schema.getId());

		return DocumentValidationResult.fromDiagnostics(diagnostics);
	}

}
