package com.fdmgroup.documentuploader.service.register;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.registration.RegistrationWrapper;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * Service class which defines behavior related to the registration and account
 * activation process for new users.
 * 
 * @author Noah Anderson
 *
 */
public interface AbstractRegisterService {

	/**
	 * Processes the registration of a new user to the system.
	 * 
	 * @param wrapper Encapsulates the {@link User}, {@link Account}, and
	 *                {@link AuthGroup} objects which are needed to successfully
	 *                complete account registration.
	 * @return User instance representing the newly registered user
	 */
	User processRegistration(RegistrationWrapper wrapper);

	/**
	 * Attempts to confirm account activation based on the {@code token} given.
	 * 
	 * @param token the unique string representing the token sent to the email used
	 *              to register an account
	 * @return {@code true} if account was successfully activated, {@code false}
	 *         otherwise
	 */
	boolean activateAccountWithToken(String token);
}
