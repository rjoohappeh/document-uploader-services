package com.fdmgroup.documentuploader.documentuploaderservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.service.authgroup.AuthGroupService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties(value = ApplicationProperties.class)
@TestPropertySource(value = { "classpath:/application.properties" })
class AuthGroupControllerTest {

	private static final String TEST_USERNAME = "testUsername";

	private static ObjectMapper objectMapper;

	private RequestUris requestUris;
	
	@MockBean
	private JavaMailSender mockJavaMailSender;

	@MockBean
	private AuthGroupService mockAuthGroupService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@BeforeAll
	private static void initObjectMapper() {
		objectMapper = new ObjectMapper();
	}
	
	@BeforeEach
	void setup() throws Exception {
		this.requestUris = applicationProperties.getRequestUris();
	}
	
	@Test
	void contextLoads() {
		
	}
	
	@Test
	void testFindAuthGroupsByUsername_returnsResultReturnedFromAuthGroupService() throws Exception {
		when(mockAuthGroupService.getAuthGroupsByUsername(TEST_USERNAME)).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(requestUris.getAuthGroup())
				.queryParam("username", TEST_USERNAME)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(matchAll(
							status().isOk(),
							content().contentType(MediaType.APPLICATION_JSON),
							content().json("[]")));
	}
}
