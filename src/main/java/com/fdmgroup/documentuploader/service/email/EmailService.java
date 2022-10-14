package com.fdmgroup.documentuploader.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.MailSettings;

/**
 * Implementing class of {@link AbstractEmailService}.
 * 
 * @author Noah Anderson
 */
@Service
public class EmailService implements AbstractEmailService {

	/**
	 * Object which performs the operation of sending an email to a user.
	 */
	private final JavaMailSender mailSender;

	/**
	 * Used to retrieve constant values used throughout the application.
	 */
	private final ApplicationProperties applicationProperties;

	@Autowired
	public EmailService(JavaMailSender mailSender, ApplicationProperties applicationProperties) {
		super();
		this.mailSender = mailSender;
		this.applicationProperties = applicationProperties;
	}

	@Override
	public void sendEmail(String to, String subject, String message) {
		SimpleMailMessage email = createEmail(to, subject, message);
		mailSender.send(email);
	}

	/**
	 * Constructs a {@link SimpleMailMessage} object based on the values passed into
	 * the method as well as email configuration properties encapsulated in the
	 * {@link #applicationProperties} object.
	 * 
	 * @param to      the recipient of the email
	 * @param subject the subject of the email
	 * @param message the body of the email
	 * @return Created {@code SimpleMailMessage} instance.
	 */
	private SimpleMailMessage createEmail(String to, String subject, String message) {
		MailSettings mailSettings = applicationProperties.getMailSettings();
		String from = mailSettings.getFrom();

		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom(from);
		email.setTo(to);
		email.setSubject(subject);
		email.setText(message);

		return email;
	}
}
