package org.elifesciences.schematron.api;

import com.google.common.io.ByteStreams;
import org.elifesciences.schematron.DocumentValidatorApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.elifesciences.schematron.api.DocumentValidationController.VALIDATE_FILE_PATH;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentValidatorApplication.class)
public class DocumentValidationControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void respondsWithBadRequestOnInvalidXml() throws Exception {
		MockMultipartFile document = new MockMultipartFile("document", "BADXML".getBytes());

		mockMvc.perform(fileUpload(VALIDATE_FILE_PATH, "sample-schema").file(document))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("is an invalid XML document")))
			.andReturn();
	}

	@Test
	public void respondsWithBadRequestOnInvalidSchema() throws Exception {
		MockMultipartFile document = new MockMultipartFile("document", "<goodxml />".getBytes());

		mockMvc.perform(fileUpload(VALIDATE_FILE_PATH, "invalid-schema-id").file(document))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("is an invalid schema")))
			.andReturn();
	}

	@Test
	public void respondsWithSuccessOnValidation() throws Exception {
		InputStream testDocumentSource = DocumentValidationControllerTest.class.getResourceAsStream("/fixtures/sample-document.xml");
		byte[] testDocument = ByteStreams.toByteArray(testDocumentSource);

		MockMultipartFile document = new MockMultipartFile("document", testDocument);

		mockMvc.perform(fileUpload(VALIDATE_FILE_PATH, "sample-schema").file(document))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}
}
