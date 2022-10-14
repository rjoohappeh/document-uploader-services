package com.fdmgroup.documentuploader.documentuploaderservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.document.DocumentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties(value = ApplicationProperties.class)
@TestPropertySource(value = { "classpath:/application.properties" })
class DocumentControllerTest {

	private static final String DOCUMENT_ID_EXISTS = "the document id given already exists.";
	private static final String CONTENT = "content";
	private static final String NAME = "name";
	private static final String EXTENSION = "extension";
	private static final String PATH_PARAM_ID = "/{id}";
	private static final String ID = "id";

	private static ObjectMapper objectMapper;

	private Document validDocument;

	private Document invalidDocument;

	private RequestUris requestUris;

	private Map<String, String> errorMap;
	
	@MockBean
	private JavaMailSender mockJavaMailSender;
	
	@MockBean
	private DocumentService mockDocumentService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@BeforeAll
	static void initObjectMapper() {
		objectMapper = new ObjectMapper();
	}
	
	@BeforeEach
	void setup() throws Exception {
		this.requestUris = applicationProperties.getRequestUris();
		this.validDocument = new Document(CONTENT.getBytes(), NAME, ".docx");
		this.invalidDocument = new Document();
		initMap();
	}
	
	void initMap() {
		this.errorMap = new HashMap<>();
		errorMap.put(CONTENT, "Document Content must not be null nor empty.");
		errorMap.put(NAME, "Document Name is required.");
		errorMap.put(EXTENSION, "Document Extension is required.");
	}
	
	@Test
	void testSaveDocument_respondsWithBadRequestAndValidationMessaged_whenInvalidDocumentIsGiven() throws Exception {
		mockMvc.perform(post(requestUris.getDocuments())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDocument)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(errorMap))));
	}
	
	@Test
	void testSaveDocument_respondsWithBadRequestAndExceptionMessage_whenExceptionIsThrownFromDocumentService() throws Exception {
		when(mockDocumentService.uploadDocument(validDocument)).thenThrow(new EntityCouldNotBeSavedException(Document.class, "the document id given already exists."));
		
		mockMvc.perform(post(requestUris.getDocuments())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(validDocument)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("The Document could not be saved for the following reason: the document id given already exists.")));
	}
	
	@Test
	void testSaveDocument_returnsSavedAccountAnd201StatusCode() throws Exception {
		when(mockDocumentService.uploadDocument(validDocument)).thenReturn(validDocument);
		
		String validDocumentJson = objectMapper.writeValueAsString(validDocument);
		mockMvc.perform(post(requestUris.getDocuments())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validDocumentJson))
					.andExpect(matchAll(
							status().isCreated(),
							content().json(validDocumentJson),
							content().contentType(MediaType.APPLICATION_JSON)));
	}
	
	@Test
	void testGetDocument_returnsResultReturnedFromDocumentServiceGetDocumentById() throws Exception {
		when(mockDocumentService.getDocumentById(0L)).thenReturn(Optional.of(validDocument));
		
		mockMvc.perform(get(requestUris.getDocuments())
				.queryParam(ID, "0")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validDocument)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}
	
	@Test
	void testGetDocument_returnsResultReturnedFromDocumentServiceGetDocumentByName() throws Exception {
		when(mockDocumentService.getDocumentByName(NAME)).thenReturn(Optional.of(validDocument));
		
		mockMvc.perform(get(requestUris.getDocuments())
				.queryParam("documentName", NAME)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validDocument)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}
	
	@Test
	void testRemoveDocumentById_respondsWithNotFoundAndExceptionMessage_whenExceptionIsThrownFromDocumentService() throws Exception {
		doThrow(new EntityNotFoundException(Document.class, ID, DOCUMENT_ID_EXISTS)).when(mockDocumentService).deleteDocumentById(0L);
		
		mockMvc.perform(delete(requestUris.getDocuments() + PATH_PARAM_ID, 0)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isNotFound(),
							content().string("No Document was found for parameters {id=the document id given already exists.}")));
	}
	
	@Test
	void testRemoveDocumentById_respondsWithNoContent_whenDocumentIsSuccessfullyDeleted() throws Exception {
		mockMvc.perform(delete(requestUris.getDocuments() + PATH_PARAM_ID, 0)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isNoContent()))
					.andDo(print());
	}
}
