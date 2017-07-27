package org.elifesciences.schematron.model;

import com.google.common.collect.ImmutableList;
import org.elifesciences.schvalidator.common.Diagnostic;
import org.elifesciences.schvalidator.common.DiagnosticLevel;
import org.junit.Test;

import static org.elifesciences.schematron.model.DocumentValidationResult.Status.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DocumentValidationResultTest {

	@Test
	public void isInvalidWithOneErrorDiagnostic() throws Exception {
		Diagnostic diagnostic = new Diagnostic("/a", "abc", DiagnosticLevel.ERROR);
		DocumentValidationResult result = DocumentValidationResult.fromDiagnostics(ImmutableList.of(diagnostic));

		assertThat(result.getStatus(), is(equalTo(INVALID)));
	}

	@Test
	public void isValidWithNoDiagnostics() throws Exception {
		DocumentValidationResult result = DocumentValidationResult.fromDiagnostics(ImmutableList.of());

		assertThat(result.getStatus(), is(equalTo(VALID)));
	}

	@Test
	public void isValidWithInfoDiagnostic() throws Exception {
		Diagnostic diagnostic = new Diagnostic("/a", "abc", DiagnosticLevel.INFO);
		DocumentValidationResult result = DocumentValidationResult.fromDiagnostics(ImmutableList.of(diagnostic));

		assertThat(result.getStatus(), is(equalTo(VALID)));
	}

	@Test
	public void isValidWithWarningsWithWarningDiagnostic() throws Exception {
		Diagnostic diagnostic = new Diagnostic("/a", "abc", DiagnosticLevel.WARNING);
		DocumentValidationResult result = DocumentValidationResult.fromDiagnostics(ImmutableList.of(diagnostic));

		assertThat(result.getStatus(), is(equalTo(VALID_WITH_WARNINGS)));
	}

}
