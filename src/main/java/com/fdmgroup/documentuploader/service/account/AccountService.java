package com.fdmgroup.documentuploader.service.account;

import com.fdmgroup.documentuploader.events.AccountDocumentEvent;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.repository.AccountRepository;
import com.fdmgroup.documentuploader.service.document.AbstractDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Implementing class of {@link AbstractAccountService} which performs
 * operations related to {@link Account} objects.
 * </p>
 *
 * @author Noah Anderson
 */
@Primary
@Service
public class AccountService implements AbstractAccountService {

	private static final String ACCOUNT = "account";
	private static final String DOCUMENT_NAME = "name";
	private static final String ID = "id";

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;
	private final AccountRepository accountRepository;
	private final AbstractDocumentService documentService;
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public AccountService(MessageSource messageSource, AccountRepository accountRepository,
			AbstractDocumentService documentService, ApplicationEventPublisher eventPublisher) {
		super();
		this.messageSource = messageSource;
		this.accountRepository = accountRepository;
		this.documentService = documentService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Account save(Account account) {
		String name = account.getName();
		boolean exists = accountRepository.existsByName(name);
		if (exists) {
			throw new EntityCouldNotBeSavedException(Account.class,
					messageSource.getMessage("account.name.is-taken", null, Locale.getDefault()) + name);
		}
		return accountRepository.save(account);
	}

	@Override
	public Account update(Account account) {
		boolean isExistingAccount = accountRepository.existsById(account.getId());
		if (!isExistingAccount) {
			throw new EntityNotFoundException(Account.class, ACCOUNT, String.valueOf(account.getId()));
		}
		return accountRepository.save(account);
	}

	@Override
	public Optional<Account> getAccountById(long id) {
		return accountRepository.findById(id);
	}

	@Override
	public List<Account> getAccountsByUserId(long userId) {
		return accountRepository.findAccountsByUserId(userId);
	}

	@Override
	public Optional<Account> getAccountByOwnerId(long ownerId) {
		return accountRepository.findByOwnerId(ownerId);
	}

	@Override
	public Optional<Account> getAccountByName(String accountName) {
		return accountRepository.findByName(accountName);
	}

	@Transactional
	@Override
	public Account addDocumentToAccountByAccountId(Document document, long accountId) {
		Account account = getAccount(accountId);
		Set<Document> accountDocuments = account.getDocuments();
		if (isDocumentInDocumentSet(accountDocuments, document)) {
			throw new EntityCouldNotBeSavedException(Document.class,
					messageSource.getMessage("account.documents.is-on-account", null, Locale.getDefault())
							+ document.getName());
		}
		Document uploadedDocument = documentService.uploadDocument(document);
		accountDocuments.add(uploadedDocument);
		account.setDocuments(accountDocuments);

		Account updatedAccount = accountRepository.save(account);
		createAndPublishAccountDocumentEvent(document, updatedAccount, true);

		return updatedAccount;
	}

	/**
	 * Retrieves an {@link Account} with the given {@code accountId}.
	 *
	 * @param accountId the {@code id} of an {@code Account}
	 * @return an {@code Account} with an {@code id} equaling the value of
	 *         {@code accountId}
	 * @throws EntityNotFoundException if no {@code Account} is found with an
	 *                                 {@code id} equaling the value of
	 *                                 {@code accountId}
	 */
	private Account getAccount(long accountId) {
		Optional<Account> optionalAccount = accountRepository.findById(accountId);
		if (!optionalAccount.isPresent()) {
			throw new EntityNotFoundException(Account.class, ID, String.valueOf(accountId));
		}
		return optionalAccount.get();
	}

	/**
	 * Checks if the given {@link Document document} exists within the given
	 * {@code Set} of {@code documents}.
	 *
	 * @param documents the {@code Set} of {@code Document} objects
	 * @param document  the {@code Document} which may or may not exist within the
	 *                  given {@code Set} of documents
	 * @return {@code true} if the given {@code document} exists within the
	 *         {@code Set} of documents. Otherwise, returns {@code false}.
	 */
	private boolean isDocumentInDocumentSet(Set<Document> documents, Document document) {
		String fileName = document.getName();
		return documents.stream().anyMatch(doc -> fileName.equals(doc.getName()));
	}

	/**
	 * Creates a {@link AccountDocumentEvent} and publishes it to the application
	 * which triggers the invocation of the
	 * {@link com.fdmgroup.documentuploader.listener.AccountDocumentEventListener#onApplicationEvent(AccountDocumentEvent)
	 * onApplicationEvent(AccountDocumentEvent)} method of
	 * {@link com.fdmgroup.documentuploader.listener.AccountDocumentEventListener
	 * AccountDocumentEventListener}.
	 *
	 * @param document          the {@link Document} which is being added to or
	 *                          removed from the given {@code account}
	 * @param account           the {@link Account} which is being modified
	 * @param wasAddedToAccount {@code true} if the given {@code document} was added
	 *                          to the given {@code account}. Otherwise, should be
	 *                          {@code false}.
	 */
	private void createAndPublishAccountDocumentEvent(Document document, Account account, boolean wasAddedToAccount) {
		AccountDocumentEvent documentAddedEvent = new AccountDocumentEvent(document, account, wasAddedToAccount);
		eventPublisher.publishEvent(documentAddedEvent);
	}

	@Transactional
	@Override
	public Account removeDocumentFromAccountByFileName(String fileName, long accountId) {
		Account account = getAccount(accountId);
		Set<Document> accountDocuments = account.getDocuments();
		Optional<Document> optionalDocument = accountDocuments.stream().filter(doc -> doc.getName().equals(fileName))
				.collect(Collectors.toList()).stream().findFirst();

		if (!optionalDocument.isPresent()) {
			throw new EntityNotFoundException(Document.class, DOCUMENT_NAME, fileName);
		}
		Document document = optionalDocument.get();
		documentService.deleteDocumentById(document.getId());
		accountDocuments.remove(document);
		account.setDocuments(accountDocuments);
		Account updatedAccount = accountRepository.save(account);

		createAndPublishAccountDocumentEvent(document, account, false);

		return updatedAccount;
	}
}
