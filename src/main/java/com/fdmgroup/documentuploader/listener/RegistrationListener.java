package com.fdmgroup.documentuploader.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.MailSettings;
import com.fdmgroup.documentuploader.events.OnRegistrationCompleteEvent;
import com.fdmgroup.documentuploader.model.registration.ConfirmationToken;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.repository.ConfirmationTokenRepository;
import com.fdmgroup.documentuploader.service.email.AbstractEmailService;

/**
 * Listener which completes an action upon detection of
 * {@link OnRegistrationCompleteEvent} instances being published by the
 * {@link org.springframework.context.ApplicationEventPublisher ApplicationEventPublisher}.
 * 
 * @author Noah Anderson
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	/**
	 * Repository which performs Database operations for {@link ConfirmationToken}
	 * instances.
	 * 
	 * @see ConfirmationTokenRepository
	 */
	private final ConfirmationTokenRepository confirmationTokenRepository;

	/**
	 * Service class which is responsible for all operations relating to emails.
	 * 
	 * @see AbstractEmailService
	 */
	private final AbstractEmailService emailService;

	/**
	 * Used to retrieve constant values used throughout the application.
	 */
	private final ApplicationProperties applicationProperties;

	@Autowired
	public RegistrationListener(ConfirmationTokenRepository confirmationTokenRepository, AbstractEmailService emailService,
			ApplicationProperties applicationProperties) {
		super();
		this.confirmationTokenRepository = confirmationTokenRepository;
		this.emailService = emailService;
		this.applicationProperties = applicationProperties;
	}

	@Async
	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		this.sendConfirmationEmail(event);
	}

	/**
	 * Sends confirmation email to the {@link User} object encapsulated in the
	 * {@link OnRegistrationCompleteEvent event} argument given.
	 * 
	 * @param event trigger which caused this listener to execute
	 */
	private void sendConfirmationEmail(OnRegistrationCompleteEvent event) {
		User user = event.getUser();
		ConfirmationToken confirmationToken = createAndSaveConfirmationToken(user);

		MailSettings mailSettings = applicationProperties.getMailSettings();
		String to = user.getEmail();
		String subject = mailSettings.getConfirmAccountSubject();
		String message = mailSettings.getConfirmAccountMessage() + applicationProperties.getHostUrl()
				+ mailSettings.getConfirmAccountPath() + confirmationToken.getToken();
		emailService.sendEmail(to, subject, message);
	}

	/**
	 * Creates a {@link ConfirmationToken} for the {@link User user} given as an
	 * argument, then saves it to the data source.
	 * 
	 * @param user user to create a {@code ConfirmationToken} for
	 * @return saved {@code ConfirmationToken}
	 */
	private ConfirmationToken createAndSaveConfirmationToken(User user) {
		ConfirmationToken confirmationToken = new ConfirmationToken(user);
		return confirmationTokenRepository.save(confirmationToken);
	}
}
