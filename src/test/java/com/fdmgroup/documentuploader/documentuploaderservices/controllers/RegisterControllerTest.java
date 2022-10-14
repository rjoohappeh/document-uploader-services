package com.fdmgroup.documentuploader.documentuploaderservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.ServiceLevel;
import com.fdmgroup.documentuploader.model.registration.RegistrationWrapper;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.register.RegisterService;
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
import java.util.HashSet;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties(value = ApplicationProperties.class)
@TestPropertySource(value = { "classpath:/application.properties" })
class RegisterControllerTest {

	private static final String TOKEN = "token";
	private static final String USER = "user";
	private static final String ACCOUNT = "account";
	private static final String AUTH_GROUP = "authGroup";

	private static ObjectMapper objectMapper;

	private RequestUris requestUris;

	private RegistrationWrapper validRegistrationWrapper;

	private RegistrationWrapper invalidRegistrationWrapper;

	private Map<String, String> errorMap;

	private Account account;

	private User user;

	private AuthGroup authGroup;
	
	@MockBean
	private JavaMailSender javaMailSender;
	
	@MockBean
	private RegisterService mockRegisterService;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeAll
	static void initObjectMapper() {
		objectMapper = new ObjectMapper();
	}
	
	@BeforeEach
	void setup() throws Exception {
		this.requestUris = applicationProperties.getRequestUris();
		this.user = new User("default", "default", "default", "default", true);
		this.account = new Account("default", user, ServiceLevel.BRONZE, new HashSet<>(), new HashSet<>());
		this.authGroup = new AuthGroup();
		this.validRegistrationWrapper = new RegistrationWrapper(user, account, authGroup);
		this.invalidRegistrationWrapper = new RegistrationWrapper();
		initMap();
	}
	
	void initMap() {
		this.errorMap = new HashMap<>();
		errorMap.put(USER, "User is required.");
		errorMap.put(ACCOUNT, "Account is required.");
		errorMap.put(AUTH_GROUP, "AuthGroup is required.");
	}
	
	@Test
	void contextLoads() {
		
	}
	
	@Test
	void testRegisterNewUser_respondsWithBadRequestAndValidationMessages_whenRegistrationWrapperIsInvalid() throws Exception {
		mockMvc.perform(post(requestUris.getRegister())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRegistrationWrapper)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(errorMap))));
	}

	@Test
	void testRegisterNewUser_respondsWithNoContent_whenRegistrationIsSuccessful() throws Exception {
		mockMvc.perform(post(requestUris.getRegister())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRegistrationWrapper)))
					.andExpect(matchAll(
							status().isNoContent()));
	}
	
	@Test
	void testConfirmRegistration_callsRegisterServiceActivateAccountWithToken() throws Exception {
		when(mockRegisterService.activateAccountWithToken(TOKEN)).thenReturn(false);
		mockMvc.perform(patch(requestUris.getRegister() + requestUris.getConfirmToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(TOKEN)))
					.andExpect(matchAll(
							status().isOk(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("false")));
		
		verify(mockRegisterService, times(1)).activateAccountWithToken("\"" + TOKEN + "\"");
	}
}
