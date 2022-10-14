package com.fdmgroup.documentuploader.repository;

import com.fdmgroup.documentuploader.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository which performs CRUD database operations on {@link User} objects.
 * 
 * @author Noah Anderson
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Retrieves a {@link User} instance with an {@code email} equaling the given
	 * {@code email}.
	 * 
	 * @param email the {@code email} of a {@code User}
	 * @return {@code empty} {@link Optional} when the given {@code email} is not
	 *         associated with any {@code User} instance. Otherwise, a
	 *         {@code Optional} wrapping the found {@code User} is returned.
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Checks if a {@link User} exists with the given email.
	 * 
	 * @param email the {@code email} to search for
	 * @return {@code true} if a {@code User} is found with the given email,
	 *         {@code false} otherwise
	 */
	boolean existsByEmail(String email);
}
