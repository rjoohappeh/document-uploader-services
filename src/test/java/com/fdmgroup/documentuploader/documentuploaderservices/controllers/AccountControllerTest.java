package com.fdmgroup.documentuploader.documentuploaderservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.ServiceLevel;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.account.AccountService;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
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
class AccountControllerTest {

	private static final String ACCOUNT = "account";
	private static final String ACCOUNT_NAME_TAKEN = "the account name given is already in use: ";
	private static final String TEST_ACCOUNT_NAME = "accountTest";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String OWNER = "owner";
	private static final String USERS = "users";
	private static final String DOCUMENTS = "documents";
	private static final String SERVICE_LEVEL = "serviceLevel";

	private static final String DOCUMENT_ALREADY_ON_ACCOUNT = "the id of the document given is already on the account: ";
	private static final String DOCUMENT_NAME = "documentName";
	private static final String CONTENT = "content";
	private static final String EXTENSION = "extension";

	private static ObjectMapper objectMapper;

	private RequestUris requestUris;

	private Account validAccount;

	private Account invalidAccount;

	private Document validDocument;

	private Document invalidDocument;

	private User validUser;

	private Map<String, String> accountErrorMap;

	private Map<String, String> documentErrorMap;

	@MockBean
	private JavaMailSender javaMailSender;

	@MockBean
	private AccountService mockAccountService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApplicationProperties applicationProperties;

	@BeforeAll
	static void init() {
		objectMapper = new ObjectMapper();
	}

	@BeforeEach
	void setup(){
		this.requestUris = applicationProperties.getRequestUris();
		this.validUser = new User();
		this.validAccount = new Account(NAME, validUser, ServiceLevel.BRONZE, Collections.emptySet(), Collections.emptySet());
		this.invalidAccount = new Account();
		this.validDocument = new Document(CONTENT.getBytes(), NAME, ".docx");
		this.invalidDocument = new Document();
		initAccountErrorMap();
	}

	void initAccountErrorMap() {
		this.accountErrorMap = new HashMap<>();
		accountErrorMap.put(NAME, "Account Name is required.");
		accountErrorMap.put(OWNER, "Account owner is required.");
		accountErrorMap.put(SERVICE_LEVEL, "Service Level is required.");
		accountErrorMap.put(USERS, "Users collection is required.");
		accountErrorMap.put(DOCUMENTS, "Documents collection is required.");
	}

	void initDocumentErrorMap() {
		this.documentErrorMap = new HashMap<>();
		documentErrorMap.put(CONTENT, "Document Content must not be null nor empty.");
		documentErrorMap.put(NAME, "Document Name is required.");
		documentErrorMap.put(EXTENSION, "Document Extension is required.");
	}

	@Test
	void testSaveAccount_respondsWithBadRequestAndValidationMessages_whenInvalidAccountIsGiven() throws Exception {
		mockMvc.perform(post(requestUris.getAccounts())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidAccount)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(accountErrorMap))));
	}

	@Test
	void testSaveAccount_throwsEntityCouldNotBeSavedException_whenExceptionIsThrownFromAccountService()
			throws Exception {
		when(mockAccountService.save(validAccount)).thenThrow(new EntityCouldNotBeSavedException(Account.class, ACCOUNT_NAME_TAKEN + TEST_ACCOUNT_NAME));

		mockMvc.perform(post(requestUris.getAccounts())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validAccount)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("The Account could not be saved for the following reason: the account name given is already in use: accountTest")));
	}

	@Test
	void testSaveAccount_returnsSavedAccountAnd201StatusCode() throws Exception {
		when(mockAccountService.save(validAccount)).thenReturn(validAccount);

		String validAccountJson = objectMapper.writeValueAsString(validAccount);
		mockMvc.perform(post(requestUris.getAccounts())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validAccountJson))
					.andExpect(matchAll(
							status().isCreated(),
							content().json(validAccountJson),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testUpdateAccount_respondsWithBadRequestAndValidationMessages_whenInvalidAccountIsGiven() throws Exception {
		mockMvc.perform(put(requestUris.getAccounts())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidAccount)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(accountErrorMap))));
	}

	@Test
	void testUpdateAccount_throwsEntityNotFoundException_whenExceptionIsThrownFromAccountService()
			throws Exception {
		when(mockAccountService.update(validAccount)).thenThrow(new EntityNotFoundException(Account.class, ACCOUNT, String.valueOf(0L)));

		mockMvc.perform(put(requestUris.getAccounts())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validAccount)))
					.andExpect(matchAll(
							status().isNotFound(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("No Account was found for parameters {account=0}")))
					.andDo(print());
		}

	@Test
	void testUpdateAccount_returnsUpdatedAccountAnd200StatusCode() throws Exception {
		when(mockAccountService.update(validAccount)).thenReturn(validAccount);

		String validAccountJson = objectMapper.writeValueAsString(validAccount);
		mockMvc.perform(put(requestUris.getAccounts())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validAccountJson))
					.andExpect(matchAll(
							status().isOk(),
							content().json(validAccountJson),
							content().contentType(MediaType.APPLICATION_JSON)))
					.andDo(print());
	}

	@Test
	void testFindByAccountId_returnsResultReturnedFromAccountServiceGetAccountById() throws Exception {
		when(mockAccountService.getAccountById(anyLong())).thenReturn(Optional.of(validAccount));

		mockMvc.perform(get(requestUris.getAccounts())
				.queryParam(ID, "1")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validAccount)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testFindByUserId_returnsResultReturnedFromAccountServiceGetAccountsByUserId() throws Exception {
		when(mockAccountService.getAccountsByUserId(anyLong())).thenReturn(Collections.emptyList());

		mockMvc.perform(get(requestUris.getAccounts())
				.queryParam("userId", "1")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(Collections.emptyList())),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testFindByOwnerId_returnsResultReturnedFromAccountServiceGetAccountByOwnerId() throws Exception {
		when(mockAccountService.getAccountByOwnerId(anyLong())).thenReturn(Optional.of(validAccount));

		mockMvc.perform(get(requestUris.getAccounts())
				.queryParam("ownerId", "1")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validAccount)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testFindByName_returnsResultReturnedFromAccountServiceGetAccountByName() throws Exception {
		when(mockAccountService.getAccountByName(TEST_ACCOUNT_NAME)).thenReturn(Optional.of(validAccount));

		mockMvc.perform(get(requestUris.getAccounts())
				.queryParam(NAME, TEST_ACCOUNT_NAME)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validAccount)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testAddDocumentToAccount_respondsWithBadRequestAndValidationMessaged_whenInvalidDocumentIsGiven() throws Exception {
		initDocumentErrorMap();

		mockMvc.perform(put(requestUris.getAccounts() + "/{id}" + requestUris.getDocuments(), 1)
				.queryParam(ID, "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDocument)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(documentErrorMap))));
	}

	@Test
	void testAddDocumentToAccount_throwsEntityNotFoundException_whenThrownByAccountService() throws Exception {
		when(mockAccountService.addDocumentToAccountByAccountId(validDocument, 1L)).thenThrow(new EntityNotFoundException(Account.class, ACCOUNT, String.valueOf(1L)));

		mockMvc.perform(put(requestUris.getAccounts() + "/{id}" + requestUris.getDocuments(), 1)
				.queryParam(ID, "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validDocument)))
					.andExpect(matchAll(
							status().isNotFound(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("No Account was found for parameters {account=1}")));
	}

	@Test
	void testAddDocumentToAccount_throwsEntityCouldNotBeSavedException_whenThrownByAccountService() throws Exception {
		when(mockAccountService.addDocumentToAccountByAccountId(validDocument, 1L)).thenThrow(new EntityCouldNotBeSavedException(Document.class, DOCUMENT_ALREADY_ON_ACCOUNT + 0L));

		mockMvc.perform(put(requestUris.getAccounts() + "/{id}" + requestUris.getDocuments(), 1)
				.queryParam(ID, "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validDocument)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("The Document could not be saved for the following reason: the id of the document given is already on the account: 0")));
	}

	@Test
	void testAddDocumentToAccount_returnsResponseFromAccountService_whenNoExceptionsAreThrown() throws Exception {
		when(mockAccountService.addDocumentToAccountByAccountId(validDocument, 1L)).thenReturn(validAccount);

		mockMvc.perform(put(requestUris.getAccounts() + "/{id}" + requestUris.getDocuments(), 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validDocument)))
					.andExpect(matchAll(
							status().isOk(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(validAccount))));
	}

	@Test
	void testRemoveDocumentFromAccount_throwsEntityNotFoundException_whenThrownByAccountService() throws Exception {
		when(mockAccountService.removeDocumentFromAccountByFileName(NAME, 1L)).thenThrow(new EntityNotFoundException(Account.class, ACCOUNT, String.valueOf(1L)));

		mockMvc.perform(delete(requestUris.getAccounts() + "/{id}" + requestUris.getDocuments(), 1)
				.queryParam(DOCUMENT_NAME, NAME)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(matchAll(
						status().isNotFound(),
						content().string("No Account was found for parameters {account=1}")));
	}

	@Test
	void testRemoveDocumentFromAccount_returnsResponseFromAccountService_whenNoExceptionIsThrown() throws Exception {
		when(mockAccountService.removeDocumentFromAccountByFileName(NAME, 1L)).thenReturn(validAccount);

		mockMvc.perform(delete(requestUris.getAccounts() + "/{id}" + requestUris.getDocuments(), 1)
				.queryParam(DOCUMENT_NAME, NAME)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(matchAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						content().json(objectMapper.writeValueAsString(validAccount))));
	}
}
