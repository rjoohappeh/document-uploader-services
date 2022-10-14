package com.fdmgroup.documentuploader.service.account;

import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to operations
 * dealing with {@link Account} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractAccountService {

	/**
	 * Saves the given {@link Account} to the data source.
	 * 
	 * @param account the {@code Account} to be saved
	 * @return the saved {@code Account} instance
	 * @throws EntityCouldNotBeSavedException when the {@code name} of the given
	 *                                        {@code Account} is already in use
	 */
	Account save(Account account);

	/**
	 * Updates the given {@link Account} with the data source.
	 * 
	 * @param account the {@code Account} to be updated
	 * @return the updated {@code Account}
	 * @throws EntityNotFoundException when the {@code account} given does not
	 *                                 already exist
	 */
	Account update(Account account);

	/**
	 * Attempts to retrieve an {@link Account} by the {@code id} given.
	 * 
	 * @param id the {@code id} of an {@code Account}
	 * @return {@code empty} {@link Optional} if no {@code Account} is found with
	 *         the given {@code id}. Otherwise, an {@code Optional} wrapping the
	 *         found {@code Account} is returned
	 */
	Optional<Account> getAccountById(long id);

	/**
	 * Retrieves all {@link Account} objects which a {@link User} with an {@code id}
	 * equaling the given {@code userId} can access.
	 * 
	 * @param userId the {@code id} of a {@code User}
	 * @return {@link List} containing all {@code Account} objects which are
	 *         accessible to a {@code User} with an {@code id} equaling the given
	 *         {@code userId}
	 */
	List<Account> getAccountsByUserId(long userId);

	/**
	 * Attempts to retrieve an {@link Account} by the {@code ownerId} given.
	 * 
	 * @param ownerId the {@code ownerId} of the {@link User} that owns an
	 *                {@code Account}
	 * @return {@code empty} {@link Optional} if no {@code Account} is found with an
	 *         owner that has the given {@code ownerId}. Otherwise, an
	 *         {@code Optional} wrapping the found {@code Account} is returned
	 */
	Optional<Account> getAccountByOwnerId(long ownerId);

	/**
	 * Attempts to retrieve an {@link Account} with the given {@code accountName}.
	 * 
	 * @param accountName the {@code accountName} of the {@code Account} to retrieve
	 * @return {@code empty} {@link Optional} if no {@code Account} is found with a
	 *         name equaling the given {@code accountName}
	 */
	Optional<Account> getAccountByName(String accountName);

	/**
	 * Adds the given {@link Document} to an {@link Account} with an {@code id}
	 * equaling the value of {@code accountId} and returns the updated
	 * {@code Account} instance.
	 * 
	 * @param accountId the {@code id} of the {@code Account} to add
	 * @return the updated {@code Account} instance.
	 * @throws EntityNotFoundException when no {@code Account} is found with the
	 *                                 given {@code accountId}
	 */
	Account addDocumentToAccountByAccountId(Document document, long accountId);

	/**
	 * Removes a file from the given {@link Account} with a name equal to
	 * {@code fileName}.
	 *
	 * @param fileName  the name of the file to remove from the {@code account}
	 * @param accountId the {@code accountId} of an {@code Account} to remove a file
	 *                  with a name equal to {@code fileName} from
	 * @return the updated {@code Account}
	 * @throws EntityNotFoundException if a file with the given {@code fileName}
	 *                                 does not exist
	 */
	Account removeDocumentFromAccountByFileName(String fileName, long accountId);
}
