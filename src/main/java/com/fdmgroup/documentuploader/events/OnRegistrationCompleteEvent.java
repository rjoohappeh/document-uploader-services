package com.fdmgroup.documentuploader.events;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.fdmgroup.documentuploader.model.user.User;

/**
 * Event created upon successful account registration which is being listened
 * for by {@link com.fdmgroup.documentuploader.listener.RegistrationListener
 * RegistrationListener}.
 * 
 * @author Noah Anderson
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3600146491542599540L;
	/**
	 * The url of the host app.
	 */
	private final String appUrl;
	/**
	 * The locale of the user who registered.
	 */
	private final Locale locale;
	/**
	 * The {@link User} which has registered.
	 */
	private final User user;

	/**
	 * Creates a new instance of {@link OnRegistrationCompleteEvent} which
	 * encapsulates the required information to properly handle this event.
	 *
	 * @param user the {@link User} that registered.
	 * @param locale the {@link Locale} of the user who registered.
	 * @param appUrl the url of the host app.
	 */
	public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
		super(user);
		this.appUrl = appUrl;
		this.locale = locale;
		this.user = user;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public User getUser() {
		return user;
	}

}
