package com.fdmgroup.documentuploader.service.user;

import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.exceptions.InvalidTokenException;
import com.fdmgroup.documentuploader.model.user.User;

import java.util.Optional;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to operations
 * dealing with {@link User} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractUserService {

	/**
	 * Attempts to retrieve an {@link User} with the given {@code userEmail}.
	 * 
	 * @param userEmail the {@code userEmail} of the {@code User} to retrieve
	 * @return {@code empty} {@link Optional} if no {@code User} is found with a
	 *         email equaling the given {@code userEmail}
	 */
	Optional<User> getUserByEmail(String userEmail);

	/**
	 * Attempts to retrieve an {@link User} with the given {@code userId}.
	 * 
	 * @param userId the {@code userId} of the {@code User} to retrieve
	 * @return {@code empty} {@link Optional} if no {@code User} is found with an id
	 *         equaling the given {@code userId}
	 */
	Optional<User> getUserById(long userId);

	/**
	 * Saves the given {@link User} to the data source.
	 *
	 * @param user the {@code User} to be saved
	 * @return the saved {@code User} instance
	 * @throws EntityCouldNotBeSavedException when the {@code email} of the given
	 *                                        {@code User} is already in use
	 */
	User save(User user);

	/**
	 * Updates the given {@link User} with the data source.
	 * 
	 * @param user the {@code User} to be updated
	 * @return the updated {@code User}
	 * @throws EntityNotFoundException when the {@code user} given does not already
	 *                                 exist
	 */
	User update(User user) throws EntityNotFoundException;

	/**
	 * Checks whether the {@link User} associated with the given {@code userEmail}
	 * has enabled their account.
	 * 
	 * @param userEmail the {@code email} associated with a {@code User}
	 * @return {@code true} if the {@code User} with the given {@code userEmail} has
	 *         enabled their account. Otherwise, returns {@code false}.
	 * @throws EntityNotFoundException when no {@code User} with an {@code email}
	 *                                 equaling {@code userEmail} is found
	 */
	Boolean isEnabledByEmail(String userEmail);

	/**
	 * Sends an email to the given {@code userEmail} which must be used to reset the
	 * password of a {@link User}.
	 *
	 * @param userEmail the {@code email} associated with a {@code User}
	 * @throws EntityNotFoundException when no {@code User} with an {@code email} *
	 *                                 equaling {@code userEmail} is found
	 */
	void sendResetPasswordEmail(String userEmail);

	/**
	 * Checks if the value of {@code passwordResetToken} matches the {@code token}
	 * of a {@link com.fdmgroup.documentuploader.model.user.PasswordResetToken
	 * PasswordResetToken} object.
	 * 
	 * @param passwordResetToken the {@code token} of a {@code PasswordResetToken}
	 *                           object
	 * @return {@code true} if the value of {@code passwordResetToken} is valid.
	 *         Otherwise, returns {@code false}.
	 */
	boolean isValidPasswordResetToken(String passwordResetToken);

	/**
	 * Changes the {@code password} of a {@link User} with an email equal to
	 * {@code userEmail}.
	 * 
	 * @param userEmail          the {@code email} associated with a {@code User}
	 * @param newPassword        the new password for the {@code User}
	 * @param passwordResetToken the {@code token} of a {@code PasswordResetToken} *
	 *                           object
	 * @throws EntityNotFoundException if no {@code User} is found an email equal to
	 *                                 {@code userEmail} or no
	 *                                 {@link com.fdmgroup.documentuploader.model.user.PasswordResetToken
	 *                                 PasswordResetToken} is found with a
	 *                                 {@code token} equal to
	 *                                 {@code passwordResetToken}.
	 * @throws InvalidTokenException   if the {@code PasswordResetToken} found with
	 *                                 a {@code token} equal to
	 *                                 {@code passwordResetToken} is expired
	 */
	void changePassword(String userEmail, String newPassword, String passwordResetToken);

}
