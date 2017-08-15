package org.elifesciences.schematron.model;


import org.elifesciences.schvalidator.common.Diagnostic;
import org.elifesciences.schvalidator.common.DiagnosticLevel;

import java.util.List;
import java.util.function.Predicate;

public final class DocumentValidationResult {

	private final Status status;
	private final List<Diagnostic> diagnostics;

	private DocumentValidationResult(Status status, List<Diagnostic> diagnostics) {
		this.status = status;
		this.diagnostics = diagnostics;
	}

	public static DocumentValidationResult fromDiagnostics(List<Diagnostic> diagnostics) {
		Status status = Status.VALID;

		boolean hasError = diagnostics.stream().anyMatch(isDiagnosticLevel(DiagnosticLevel.ERROR));
		boolean hasWarning = diagnostics.stream().anyMatch(isDiagnosticLevel(DiagnosticLevel.WARNING));

		if (hasError) {
			status = Status.INVALID;
		} else if (hasWarning) {
			status = Status.VALID_WITH_WARNINGS;
		}

		return new DocumentValidationResult(status, diagnostics);
	}

	private static Predicate<Diagnostic> isDiagnosticLevel(DiagnosticLevel level) {
		return diagnostic -> diagnostic.getLevel() == level;
	}

	public List<Diagnostic> getDiagnostics() {
		return diagnostics;
	}

	public Status getStatus() {
		return status;
	}

	public enum Status {
		INVALID,
		VALID_WITH_WARNINGS,
		VALID
	}
}
