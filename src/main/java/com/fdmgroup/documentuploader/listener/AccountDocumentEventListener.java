package com.fdmgroup.documentuploader.listener;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.events.AccountDocumentEvent;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.email.AbstractEmailService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Listener which completes an action upon detection of
 * {@link AccountDocumentEvent} instances being published by the
 * {@link org.springframework.context.ApplicationEventPublisher ApplicationEventPublisher}.
 * 
 * @author Noah Anderson
 */
@Component
public class AccountDocumentEventListener implements ApplicationListener<AccountDocumentEvent> {

	private static final String FILE_ADDED_SUBJECT = "A File Has Been Added To One Of Your Accounts";
	private static final String FILE_REMOVED_SUBJECT = "A File Has Been Removed From One Of Your Accounts";
	private static final String A_FILE_NAMED = "A file named ";
	private static final String HAS_BEEN_ADDED = " has been added to the account named ";
	private static final String HAS_BEEN_REMOVED = " has been removed from the account named ";
	
	/**
	 * Service class which is responsible for all operations relating to emails.
	 */
	private final AbstractEmailService emailService;

	/**
	 * Used to retrieve constant values used throughout the application.
	 */
	private final ApplicationProperties applicationProperties;
	
	@Autowired
	public AccountDocumentEventListener(AbstractEmailService emailService, ApplicationProperties applicationProperties) {
		super();
		this.emailService = emailService;
		this.applicationProperties = applicationProperties;
	}

	@Async
	@Override
	public void onApplicationEvent(AccountDocumentEvent event) {
		this.sendDocumentEventEmail(event);
	}
	
	/**
	 * Sends an email to the emails of each {@link User} on the {@link Account}
	 * which had a {@link Document} added to or removed from itself.
	 * 
	 * @param event trigger which caused this listener to execute
	 */
	private void sendDocumentEventEmail(AccountDocumentEvent event) {
		Document document = event.getDocument();
		Account account = event.getAccount();
		boolean wasAddedToAccount = event.wasAddedToAccount();
		
		String subject = wasAddedToAccount ? FILE_ADDED_SUBJECT : FILE_REMOVED_SUBJECT;
		String message = createEmailMessage(document, account, wasAddedToAccount);
		List<String> userEmails = getEmailsOfUsersOnAccount(account);
		
		sendEmailToUsers(userEmails, subject, message);
	}
	
	/**
	 * Gets the emails of all {@link User} objects on the given {@link Account};
	 * 
	 * @param account the {@code Account} object to get the list of user emails from
	 * @return a {@link List} containing the {@code email} of each {@code User} on
	 *         the given {@code Account}
	 */
	private List<String> getEmailsOfUsersOnAccount(Account account) {
		return account.getUsers().stream()
				.map(User::getEmail)
				.collect(Collectors.toList());
	}

	/**
	 * Creates the message of the email to be sent based on the given
	 * {@link Document}, {@link Account}, and value of {@code wasAddedToAccount}
	 * 
	 * @param document          the {@code Document} which was added to the given
	 *                          {@code Account}
	 * @param account           the {@code Account} being used
	 * @param wasAddedToAccount should equal {@code true} if the given
	 *                          {@code document} was added to the given
	 *                          {@code account}. Otherwise, should equal
	 *                          {@code false}
	 * @return the created message
	 */
	private String createEmailMessage(Document document, Account account, boolean wasAddedToAccount) {
		StringBuilder sb = new StringBuilder();
		sb.append(A_FILE_NAMED);
		
		String documentName = document.getName();
		sb.append(Strings.quote(documentName));
		
		if (wasAddedToAccount) {
			sb.append(HAS_BEEN_ADDED);
		} else {
			sb.append(HAS_BEEN_REMOVED);
		}
		
		String accountName = account.getName();
		sb.append(Strings.quote(accountName));
		
		sb.append(Strings.LINE_SEPARATOR);

		String hostUrl = applicationProperties.getHostUrl();
		
		sb.append("Click ");
		sb.append(hostUrl);
		sb.append("/login");
		sb.append(" to login to the application and view your account!");
		
		return sb.toString();
	}
	
	/**
	 * Sends an email with the given {@code subject} and {@code message} to each
	 * email in {@code userEmails}.
	 * 
	 * @param userEmails the emails of all {@link User users} to send an email to
	 * @param subject    the subject of the email
	 * @param message    the message of the email
	 */
	private void sendEmailToUsers(List<String> userEmails, String subject, String message) {
		userEmails.forEach(email -> emailService.sendEmail(email, subject, message));
	}
}
