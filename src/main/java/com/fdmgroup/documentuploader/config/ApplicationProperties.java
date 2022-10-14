package com.fdmgroup.documentuploader.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Encapsulates all constant values to be used throughout the application which
 * are located in any of the {@code .properties} files within the project
 * hierarchy.
 * 
 * @author Noah Anderson
 *
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

	private final RequestUris requestUris;
	private final MailSettings mailSettings;
	private final String hostUrl;

	public ApplicationProperties(RequestUris requestUris, MailSettings mailSettings, String hostUrl) {
		super();
		this.requestUris = requestUris;
		this.mailSettings = mailSettings;
		this.hostUrl = hostUrl;
	}

	public RequestUris getRequestUris() {
		return requestUris;
	}

	public MailSettings getMailSettings() {
		return mailSettings;
	}

	public String getHostUrl() {
		return hostUrl;
	}

	/**
	 * Static inner class of {@link ApplicationProperties} which encapsulates all
	 * request uris.
	 * 
	 * @author Noah Anderson
	 *
	 */
	public static class RequestUris {

		private final String users;
		private final String accounts;
		private final String register;
		private final String documents;
		private final String authGroup;
		private final String isEnabled;
		private final String confirmToken;
		private final String resetPassword;
		private final String token;

		public RequestUris(String users, String accounts, String register, String documents, String authGroup,
				String isEnabled, String confirmToken, String resetPassword, String token) {
			this.users = users;
			this.accounts = accounts;
			this.register = register;
			this.documents = documents;
			this.authGroup = authGroup;
			this.isEnabled = isEnabled;
			this.confirmToken = confirmToken;
			this.resetPassword = resetPassword;
			this.token = token;
		}

		public String getUsers() {
			return users;
		}

		public String getAccounts() {
			return accounts;
		}

		public String getRegister() {
			return register;
		}

		public String getDocuments() {
			return documents;
		}

		public String getAuthGroup() {
			return authGroup;
		}

		public String getIsEnabled() {
			return isEnabled;
		}

		public String getConfirmToken() {
			return confirmToken;
		}

		public String getResetPassword() {
			return resetPassword;
		}

		public String getToken() {
			return token;
		}
	}

	/**
	 * Static inner class of {@link ApplicationProperties} which encapsulates all
	 * information related to emails.
	 * 
	 * @author Noah Anderson
	 *
	 */
	public static class MailSettings {

		private final String from;
		private final String confirmAccountPath;
		private final String confirmAccountSubject;
		private final String confirmAccountMessage;
		private final String resetPasswordSubject;
		private final String resetPasswordMessage;

		@Autowired
		public MailSettings(String from, String confirmAccountPath, String confirmAccountSubject,
				String confirmAccountMessage, String resetPasswordSubject, String resetPasswordMessage) {
			super();
			this.from = from;
			this.confirmAccountPath = confirmAccountPath;
			this.confirmAccountSubject = confirmAccountSubject;
			this.confirmAccountMessage = confirmAccountMessage;
			this.resetPasswordSubject = resetPasswordSubject;
			this.resetPasswordMessage = resetPasswordMessage;
		}

		public String getFrom() {
			return from;
		}

		public String getConfirmAccountPath() {
			return confirmAccountPath;
		}

		public String getConfirmAccountSubject() {
			return confirmAccountSubject;
		}

		public String getConfirmAccountMessage() {
			return confirmAccountMessage;
		}

		public String getResetPasswordSubject() {
			return resetPasswordSubject;
		}

		public String getResetPasswordMessage() {
			return resetPasswordMessage;
		}
	}

}
