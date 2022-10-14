package com.fdmgroup.documentuploader.documentuploaderservices.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.MailSettings;
import com.fdmgroup.documentuploader.service.email.EmailService;

class EmailServiceTest {

	private static final String FROM = "from";
	private static final String TEST = "test";

	private EmailService emailService;

	private SimpleMailMessage email;
	
	@Mock
	private JavaMailSender mockJavaMailSender;
	
	@Mock
	private ApplicationProperties mockApplicationProperties;
	
	@Mock
	private MailSettings mockMailSettings;
	
	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.emailService = new EmailService(mockJavaMailSender, mockApplicationProperties);
		this.email = new SimpleMailMessage();
	}
	
	@Test
	void testSendEmail_callsGetMailSettingsGetFrom_andSend() {
		when(mockApplicationProperties.getMailSettings()).thenReturn(mockMailSettings);
		when(mockMailSettings.getFrom()).thenReturn(FROM);
		
		email.setFrom(FROM);
		email.setTo(TEST);
		email.setSubject(TEST);
		email.setText(TEST);

		emailService.sendEmail(TEST, TEST, TEST);
		
		verify(mockApplicationProperties).getMailSettings();
		verify(mockMailSettings).getFrom();
		verify(mockJavaMailSender).send(email);
	}
}
