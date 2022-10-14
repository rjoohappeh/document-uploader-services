package com.fdmgroup.documentuploader.documentuploaderservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.apache.logging.log4j.util.Strings;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties(value = ApplicationProperties.class)
@TestPropertySource(value = { "classpath:/application.properties" })
class UserControllerTest {

	private static final String TEST_EMAIL = "test@email.com";
	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";
	private static final String ID = "id";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String TEST_TOKEN = "testToken123";

	private static ObjectMapper objectMapper;

	private User validUser;
	private User invalidUser;

	private RequestUris requestUris;

	private Map<String, String> errorMap;

	@MockBean
	private UserService mockUserService;

	@MockBean
	private JavaMailSender javaMailSender;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	static void initObjectMapper() {
		objectMapper = new ObjectMapper();
	}

	@BeforeEach
	void setup() {
		this.validUser = new User(EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, false);
		this.invalidUser = new User();
		this.requestUris = applicationProperties.getRequestUris();
		initMap();
	}

	void initMap() {
		this.errorMap = new HashMap<>();
		errorMap.put(EMAIL, "Email is required.");
		errorMap.put(PASSWORD, "Password is required.");
		errorMap.put(FIRST_NAME, "First Name is required.");
		errorMap.put(LAST_NAME, "Last Name is required.");
	}

	@Test
	void contextLoads() {

	}

	@Test
	void testGetUser_returnsResultReturnedFromUserServiceGetUserByEmail() throws Exception {
		when(mockUserService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(validUser));

		mockMvc.perform(get(requestUris.getUsers())
				.queryParam(EMAIL, TEST_EMAIL)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validUser)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testGetUser_returnsResultReturnedFromUserServiceGetUserById() throws Exception {
		when(mockUserService.getUserById(0L)).thenReturn(Optional.of(validUser));

		mockMvc.perform(get(requestUris.getUsers())
				.queryParam(ID, "0")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().json(objectMapper.writeValueAsString(validUser)),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testUpdate_respondsWithBadRequestAndValidationMessages_whenInvalidUserIsGiven() throws Exception {
		mockMvc.perform(put(requestUris.getUsers())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidUser)))
					.andExpect(matchAll(
							status().isBadRequest(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(errorMap))));
	}

	@Test
	void testUpdate_throwsEntityNotFoundException_whenUserServiceUpdateThrowsIt() throws Exception {
		when(mockUserService.update(validUser)).thenThrow(new EntityNotFoundException(User.class, EMAIL, validUser.getEmail()));

		mockMvc.perform(put(requestUris.getUsers())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validUser)))
					.andExpect(matchAll(
							status().isNotFound(),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testUpdate_returnsResultReturnedFromUserServiceUpdate() throws Exception {
		when(mockUserService.update(validUser)).thenReturn(validUser);

		mockMvc.perform(put(requestUris.getUsers())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validUser)))
					.andExpect(matchAll(
							status().isOk(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json(objectMapper.writeValueAsString(validUser))));
	}

	@Test
	void testIsEnabled_respondsWithNotFound_whenUserServiceUpdate_throwsEntityNotFoundException()
			throws Exception {
		when(mockUserService.isEnabledByEmail(validUser.getEmail())).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(get(requestUris.getUsers() + "/{email}" + requestUris.getIsEnabled(), validUser.getEmail())
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isNotFound(),
							content().contentType(MediaType.APPLICATION_JSON)));
	}

	@Test
	void testIsEnabled_returnsResultReturnedFromUserServiceIsEnabledByEmail() throws Exception {
		when(mockUserService.isEnabledByEmail(validUser.getEmail())).thenReturn(true);

		mockMvc.perform(get(requestUris.getUsers() + "/{email}" + requestUris.getIsEnabled(), validUser.getEmail())
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().string("true")));
	}

	@Test
	void testIsValidPasswordResetToken_respondsCorrectlyWhenEmptyStringIsGiven() throws Exception {
		mockMvc.perform(get(requestUris.getUsers() + requestUris.getResetPassword() + requestUris.getToken())
				.queryParam("token", Strings.EMPTY)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(matchAll(
						status().isBadRequest(),
						content().contentType(MediaType.APPLICATION_JSON),
						content().string("isValidPasswordResetToken.passwordResetToken: Token is required.")
						));
	}

	@Test
	void testIsValidPasswordResetToken_respondsWithResultReturnedFromUserServiceIsValidPasswordResetToken() throws Exception {
		when(mockUserService.isValidPasswordResetToken(TEST_TOKEN)).thenReturn(true);

		mockMvc.perform(get(requestUris.getUsers() + requestUris.getResetPassword() + requestUris.getToken())
				.queryParam("token", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(result -> verify(mockUserService, times(1))
										.isValidPasswordResetToken(TEST_TOKEN))
				.andExpect(matchAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						content().string("true")
				));
	}
}
