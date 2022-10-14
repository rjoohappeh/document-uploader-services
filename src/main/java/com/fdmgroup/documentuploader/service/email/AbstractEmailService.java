package com.fdmgroup.documentuploader.service.email;

/**
 * Service class which defines operations to send emails based on the
 * information about the email to send that is given.
 * 
 * @author Noah Anderson
 *
 */
public interface AbstractEmailService {

	/**
	 * Sends an email to the value of {@code to} with a subject equal to the value
	 * of {@code subject} and content equal to the value of {@code content}
	 * 
	 * @param to      the recipient of the email
	 * @param subject the subject of the email
	 * @param message the body of the email
	 */
	void sendEmail(String to, String subject, String message);

}
