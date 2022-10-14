package com.fdmgroup.documentuploader.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.MailSettings;
import com.fdmgroup.documentuploader.events.PasswordResetEvent;
import com.fdmgroup.documentuploader.model.registration.ConfirmationToken;
import com.fdmgroup.documentuploader.model.user.PasswordResetToken;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.repository.ConfirmationTokenRepository;
import com.fdmgroup.documentuploader.repository.PasswordResetTokenRepository;
import com.fdmgroup.documentuploader.service.email.AbstractEmailService;
import org.springframework.stereotype.Component;

/**
 * Listener which completes an action upon detection of
 * {@link PasswordResetEvent} instances being published by the
 * {@link org.springframework.context.ApplicationEventPublisher ApplicationEventPublisher}.
 *
 * @author Noah Anderson
 */
@Component
public class PasswordResetEventListener implements ApplicationListener<PasswordResetEvent> {

    /**
     * Repository which performs Database operations for {@link ConfirmationToken}
     * instances.
     *
     * @see ConfirmationTokenRepository
     */
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Service class which is responsible for all operations relating to emails.
     */
    private final AbstractEmailService emailService;

    /**
     * Used to retrieve constant values used throughout the application.
     */
    private final ApplicationProperties applicationProperties;

    @Autowired
    public PasswordResetEventListener(PasswordResetTokenRepository passwordResetTokenRepository, AbstractEmailService emailService, ApplicationProperties applicationProperties) {
        super();
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void onApplicationEvent(PasswordResetEvent event) {
        this.sendPasswordResetEmail(event);
    }

    /**
     * Sends an email to the email of the user retrieved from {@link PasswordResetEvent#getUser()}
     * which must be used by the {@link User} with that email.
     *
     * @param event trigger which caused this listener to execute
     */
    private void sendPasswordResetEmail(PasswordResetEvent event) {
        User user = event.getUser();
        PasswordResetToken passwordResetToken = createAndSavePasswordResetToken(user);

        MailSettings mailSettings = applicationProperties.getMailSettings();
        String to = user.getEmail();
        String subject = mailSettings.getResetPasswordSubject();
        String message = mailSettings.getResetPasswordMessage() +  applicationProperties.getHostUrl()
                + "/user/changePassword?token=" + passwordResetToken.getToken();
        emailService.sendEmail(to, subject, message);
    }

    /**
     * Creates a {@link PasswordResetToken} for the {@link User user} given as an
     * argument, then saves it to the data source.
     *
     * @param user user to create a {@code PasswordResetToken} for
     * @return saved {@code PasswordResetToken }
     */
    private PasswordResetToken createAndSavePasswordResetToken(User user) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        return passwordResetTokenRepository.save(passwordResetToken);
    }
}
