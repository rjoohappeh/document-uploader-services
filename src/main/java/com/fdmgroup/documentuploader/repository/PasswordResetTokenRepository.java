package com.fdmgroup.documentuploader.repository;

import com.fdmgroup.documentuploader.model.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository which performs database operations on {@link PasswordResetToken}
 * objects such as storing and retrieval.
 *
 * @author Noah Anderson
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	/**
	 * Attempts to find and retrieve a {@link PasswordResetToken} with a
	 * {@code token} matching the one given.
	 * 
	 * @param token a possible {@code token} of a {@code PasswordResetToken}
	 * @return {@link Optional} wrapping the found {@code PasswordResetToken} if one
	 *         is found with the given {@code token}. Otherwise, an empty
	 *         {@code Optional} is returned.
	 */
	Optional<PasswordResetToken> findByToken(String token);
}
