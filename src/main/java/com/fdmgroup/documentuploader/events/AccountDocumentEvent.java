package com.fdmgroup.documentuploader.events;

import org.springframework.context.ApplicationEvent;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;

/**
 * Event created when a {@link Document} is added to or removed from an
 * {@link Account}.
 * 
 * @author Noah Aderson
 */
public class AccountDocumentEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6267548590667749842L;

	/**
	 * The {@link Document} which was added to or removed from the
	 * {@link AccountDocumentEvent#account account}.
	 */
	private final Document document;
	/**
	 * The {@link Account} which had a {@link Document} added to or removed from it.
	 */
	private final Account account;
	/**
	 * {@code true} if the {@link AccountDocumentEvent#document document} was added
	 * to the {@link AccountDocumentEvent#account account}, {@code false} otherwise.
	 */
	private final boolean wasAddedToAccount;

	/**
	 * Creates a new instance of {@link AccountDocumentEvent} which encapsulates the
	 * required information to properly handle this event.
	 * 
	 * @param document          the {@link Document} which was added or removed from
	 *                          the given {@code account}
	 * @param account           the {@link Account} which the given {@code document}
	 *                          was added to or removed from
	 * @param wasAddedToAccount {@code true} if the given {@code document} was added
	 *                          to the given {@code account}. Otherwise, should be
	 *                          {@code false}.
	 */
	public AccountDocumentEvent(Document document, Account account, boolean wasAddedToAccount) {
		super(document);
		this.document = document;
		this.account = account;
		this.wasAddedToAccount = wasAddedToAccount;
	}

	public Document getDocument() {
		return document;
	}

	public Account getAccount() {
		return account;
	}

	public boolean wasAddedToAccount() {
		return wasAddedToAccount;
	}

}
