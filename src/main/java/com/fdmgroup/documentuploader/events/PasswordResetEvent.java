package com.fdmgroup.documentuploader.events;

import org.springframework.context.ApplicationEvent;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * Event created when a request to reset the password of a {@link User} is
 * successfully processed. for by
 * {@link com.fdmgroup.documentuploader.listener.PasswordResetEventListener
 * PasswordResetEventListener}.
 *
 * @author Noah Aderson
 */
public class PasswordResetEvent extends ApplicationEvent {

	/**
	 * The {@link User} that requested to reset its password.
	 */
	private final User user;
	/**
	 * The url of the host app.
	 */
	private final String appUrl;

	/**
	 * Creates a new instance of {@link PasswordResetEvent} which encapsulates the
	 * required information to properly handle this event.
	 * 
	 * @param user   The {@link User} that requested to reset its password.
	 * @param appUrl The url of the host app.
	 */
	public PasswordResetEvent(User user, String appUrl) {
		super(user);
		this.user = user;
		this.appUrl = appUrl;
	}

	public User getUser() {
		return user;
	}

	public String getAppUrl() {
		return appUrl;
	}

}
