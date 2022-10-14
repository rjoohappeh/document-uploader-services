package com.fdmgroup.documentuploader.repository;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository which performs CRUD database operations on {@link Account}
 * objects.
 * 
 * @author Noah Anderson
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	/**
	 * Attempts to retrieve an {@link Account} instance which is owned by a
	 * {@link User} with an {@code id} equaling the value of {@code ownerId}.
	 * 
	 * @param id the {@code User} {@code id} to search for an {@code Account}
	 *                object with
	 * @return {@code empty} {@link Optional} if no account is found which is owned
	 *         by a {@code User} with an id of {@code ownerId}. Otherwise, an
	 *         {@code Optional} encapsulating the found {@code Account} object is
	 *         returned
	 */
	Optional<Account> findByOwnerId(long id);
	
	/**
	 * Retrieves all {@link Account} objects which a {@link User} with an {@code id}
	 * equaling the given {@code userId} can access.
	 * 
	 * @param id the {@code id} of a {@code User}
	 * @return {@link List} containing all {@code Account} objects which are
	 *         accessible to a {@code User} with an {@code id} equaling the given
	 *         {@code userId}
	 */
	@Query(value="SELECT a.account_id, a.name, a.owner_id, a.service_level FROM ACCOUNT a "
									+ "JOIN ACCOUNT_USERS au "
									+ "ON a.account_id = au.account_id "
									+ "WHERE au.user_id = :id", nativeQuery=true)
	List<Account> findAccountsByUserId(@Param("id") long id);
	
	/**
	 * Attempts to retrieve an {@link Account} instance with a name equaling the
	 * value of the given {@code name}.
	 * 
	 * @param name the {@code Account} name to search for
	 * @return {@code empty} {@link Optional} if no account is found with a name
	 *         equaling the given {@code name}. Otherwise, an {@code Optional}
	 *         encapsulating the found {@code Account} object is returned
	 */
	Optional<Account> findByName(String name);

	/**
	 * Checks if a {@link Account} exists with the given email.
	 *
	 * @param name the account {@code name} to search for
	 * @return {@code true} if a {@code Account} is found with the given name,
	 *         {@code false} otherwise
	 */
	boolean existsByName(String name);
}
