package com.fdmgroup.documentuploader.model.registration;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fdmgroup.documentuploader.model.user.User;

/**
 * Registration confirmation token created upon successful account creation. 
 * An email is sent to the email used to create the account which includes a 
 * link a client must use to activate their account.
 * 
 * @author Noah Anderson
 * @see com.fdmgroup.documentuploader.events.OnRegistrationCompleteEvent OnRegistrationCompleteEvent
 * @see com.fdmgroup.documentuploader.listener.RegistrationListener RegistrationListener
 */
@Entity
public class ConfirmationToken {

	private static final int EXPIRATION = 60 * 24;

	@Id
	@GeneratedValue(generator = "token_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "token_gen", sequenceName = "token_seq", allocationSize = 1)
	@Column(name = "user_id")
	private long id;

	@Column(name = "token", unique = true, nullable = false, updatable = false)
	private String token;

	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	public ConfirmationToken() {
		super();
	}

	public ConfirmationToken(User user) {
		super();
		this.user = user;
		this.expiryDate = getExpiryDate(EXPIRATION);
		this.token = UUID.randomUUID().toString();
	}

	private Date getExpiryDate(int expiryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);

		return new Date(cal.getTime().getTime());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
