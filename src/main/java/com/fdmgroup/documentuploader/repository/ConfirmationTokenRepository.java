package com.fdmgroup.documentuploader.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.documentuploader.model.registration.ConfirmationToken;

/**
 * Repository which performs database operations on {@link ConfirmationToken} objects
 * such as storing and retrieval.
 * 
 * @author Noah Anderson
 */
@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
	
	/**
	 * Searches the data source for a {@link ConfirmationToken} with a {@code token} value
	 * equal to the token given.
	 * 
	 * @param token the key of {@code ConfirmationToken} to search for
	 * @return Empty optional if no {@code ConfirmationToken token} is found with the given
	 * 			{@code token} or a Optional containing the found {@code ConfirmationToken token}
	 * 			object otherwise.
	 */
	Optional<ConfirmationToken> findByToken(String token);
	
}
