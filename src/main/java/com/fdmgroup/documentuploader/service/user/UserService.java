package com.fdmgroup.documentuploader.service.user;

import com.fdmgroup.documentuploader.events.PasswordResetEvent;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.exceptions.InvalidTokenException;
import com.fdmgroup.documentuploader.model.user.PasswordResetToken;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.repository.PasswordResetTokenRepository;
import com.fdmgroup.documentuploader.repository.UserRepository;
import com.fdmgroup.documentuploader.service.account.AbstractAccountService;
import com.fdmgroup.documentuploader.util.ServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Optional;

/**
 * <p>
 * Implementing class of {@link AbstractAccountService} which performs
 * operations related to {@link User} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class UserService implements AbstractUserService {

	private static final String EMAIL = "email";

	private final UserRepository userRepository;
	private final MessageSource messageSource;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public UserService(UserRepository userRepository, MessageSource messageSource,
					   PasswordResetTokenRepository passwordResetTokenRepository,
			ApplicationEventPublisher eventPublisher) {
		super();
		this.userRepository = userRepository;
		this.messageSource = messageSource;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Optional<User> getUserByEmail(String userEmail) {
		return userRepository.findByEmail(userEmail);
	}

	@Override
	public Optional<User> getUserById(long userId) {
		return userRepository.findById(userId);
	}

	@Override
	public User save(User user) {
		String email = user.getEmail();
		boolean exists = userRepository.existsByEmail(email);
		if (exists) {
			throw new EntityCouldNotBeSavedException(User.class,
					messageSource.getMessage("user.email.is-taken", null, Locale.getDefault()) + email);
		}
		return userRepository.save(user);
	}

	@Override
	public User update(User user) {
		String email = user.getEmail();
		User userToUpdate = getUserWithEmail(email);
		userToUpdate.setPassword(user.getPassword());

		return userRepository.save(userToUpdate);
	}

	/**
	 * Gets a {@link User} object with an {@code email} equaling the value of the
	 * given {@code email}.
	 *
	 * @param email the {@code email} associated with a {@code User}
	 * @return a {@code User} object with an {@code email} equaling the value of the
	 *         given {@code userEmail}
	 * @throws EntityNotFoundException when no {@code User} with an {@code email}
	 *                                 equaling {@code email} is found
	 */
	private User getUserWithEmail(String email) {
		Optional<User> optionalUser = this.getUserByEmail(email);
		if (!optionalUser.isPresent()) {
			throw new EntityNotFoundException(User.class, EMAIL, email);
		}
		return optionalUser.get();
	}

	@Override
	public Boolean isEnabledByEmail(String email) {
		User user = getUserWithEmail(email);
		return user.isEnabled();
	}

	@Override
	public void sendResetPasswordEmail(String email) {
		User user = getUserWithEmail(email);
		this.createAndPublishPasswordResetEvent(user);
	}

	/**
	 * Creates a {@link PasswordResetEvent} and publishes it to the application
	 * which triggers the invocation of the
	 * {@link com.fdmgroup.documentuploader.listener.PasswordResetEventListener#onApplicationEvent(PasswordResetEvent)
	 * onApplicationEvent(PasswordResetEvent)} method of
	 * {@link com.fdmgroup.documentuploader.listener.PasswordResetEventListener
	 * PasswordResetEventListener}.
	 *
	 * @param user the {@link User} which has created a new system account
	 */
	private void createAndPublishPasswordResetEvent(User user) {
		Optional<HttpServletRequest> optionalRequest = ServletRequestUtil.getCurrentHttpRequest();
		if (optionalRequest.isPresent()) {
			HttpServletRequest request = optionalRequest.get();
			String appUrl = request.getContextPath();
			eventPublisher.publishEvent(new PasswordResetEvent(user, appUrl));
		}
	}

	@Override
	public boolean isValidPasswordResetToken(String passwordResetToken) {
		Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository
				.findByToken(passwordResetToken);
		if (!optionalPasswordResetToken.isPresent()) {
			return false;
		}

		PasswordResetToken token = optionalPasswordResetToken.get();
		return !token.isExpired();
	}

	@Transactional
	@Override
	public void changePassword(String userEmail, String newPassword, String passwordResetToken) {
		Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository
				.findByToken(passwordResetToken);
		if (!optionalPasswordResetToken.isPresent()) {
			throw new EntityNotFoundException(PasswordResetToken.class, "token", passwordResetToken);
		}

		PasswordResetToken token = optionalPasswordResetToken.get();
		User user = token.getUser();
		String tokenEmail = user.getEmail();
		if (token.isExpired() || token.isUsed() || (tokenEmail != null && !tokenEmail.equals(userEmail))) {
			throw new InvalidTokenException(PasswordResetToken.class, passwordResetToken);
		}

		user.setPassword(newPassword);
		userRepository.save(user);

		token.setUsed(true);
		passwordResetTokenRepository.save(token);
	}
}
