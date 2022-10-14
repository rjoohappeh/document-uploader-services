package com.fdmgroup.documentuploader.service.register;

import com.fdmgroup.documentuploader.events.OnRegistrationCompleteEvent;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.registration.ConfirmationToken;
import com.fdmgroup.documentuploader.model.registration.RegistrationWrapper;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.repository.ConfirmationTokenRepository;
import com.fdmgroup.documentuploader.service.account.AbstractAccountService;
import com.fdmgroup.documentuploader.service.authgroup.AbstractAuthGroupService;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import com.fdmgroup.documentuploader.util.ServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementing class of {@link AbstractRegisterService}.
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class RegisterService implements AbstractRegisterService {

	private final AbstractAccountService accountService;
	private final AbstractUserService userService;
	private final AbstractAuthGroupService authGroupService;
	private final ConfirmationTokenRepository confirmationTokenRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public RegisterService(AbstractAccountService accountService, AbstractUserService userService,
			AbstractAuthGroupService authGroupService, ConfirmationTokenRepository confirmationTokenRepository,
			ApplicationEventPublisher eventPublisher) {
		this.accountService = accountService;
		this.userService = userService;
		this.authGroupService = authGroupService;
		this.confirmationTokenRepository = confirmationTokenRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public User processRegistration(RegistrationWrapper wrapper) {
		Account account = wrapper.getAccount();
		User user = wrapper.getUser();

		Set<User> users = new HashSet<>();
		users.add(user);

		account.setUsers(users);
		account.setOwner(user);

		AuthGroup authGroup = wrapper.getAuthGroup();

		user = userService.save(user);
		authGroupService.save(authGroup);
		accountService.save(account);

		this.createAndPublishOnRegistrationCompleteEvent(user);

		return user;
	}

	/**
	 * Creates a {@link OnRegistrationCompleteEvent} and publishes it to the
	 * application which triggers the invocation of the
	 * {@link com.fdmgroup.documentuploader.listener.RegistrationListener#onApplicationEvent(OnRegistrationCompleteEvent)
	 * onApplicationEvent(OnRegistrationCompleteEvent)} method of
	 * {@link com.fdmgroup.documentuploader.listener.RegistrationListener
	 * RegistrationListener}.
	 * 
	 * @param user the {@link User} which has created a new system account
	 */
	private void createAndPublishOnRegistrationCompleteEvent(User user) {
		Optional<HttpServletRequest> optionalRequest = ServletRequestUtil.getCurrentHttpRequest();
		if (optionalRequest.isPresent()) {
			HttpServletRequest request = optionalRequest.get();
			String appUrl = request.getContextPath();
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
		}
	}

	@Transactional
	@Override
	public boolean activateAccountWithToken(String token) {
		Optional<ConfirmationToken> optionalToken = confirmationTokenRepository.findByToken(token);
		if (!optionalToken.isPresent()) {
			return false;
		}

		ConfirmationToken confirmationToken = optionalToken.get();
		if (isExpiredToken(confirmationToken)) {
			return false;
		}

		User user = confirmationToken.getUser();
		user.setEnabled(true);
		userService.update(user);
		confirmationTokenRepository.delete(confirmationToken);

		return true;
	}

	/**
	 * Checks if the {@link ConfirmationToken} instance given has expired.
	 * 
	 * @param confirmationToken the ConfirmationToken to check
	 * @return {@code true} if the token is expired, {@code false} otherwise.
	 */
	private boolean isExpiredToken(ConfirmationToken confirmationToken) {
		Calendar calendar = Calendar.getInstance();
		return confirmationToken.getExpiryDate().getTime() - calendar.getTime().getTime() <= 0;
	}
}
