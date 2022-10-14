package com.fdmgroup.documentuploader.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Encapsulates information related to a user which exists within the system.
 * 
 * @author Noah Anderson
 */
@ApiModel(description = "Represents a logged in user of the application.")
@Entity
@Table(name = "USER_TABLE")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -445793406599580488L;

	@ApiModelProperty(notes = "Unique identifier of the User.",
			example = "1", required = true)
	@Id
	@GeneratedValue(generator = "user_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "user_gen", sequenceName = "user_seq", allocationSize = 1)
	@Column(name = "user_id")
	private long id;

	@ApiModelProperty(notes = "The email of the User.",
		example = "email@gmail.com", required = true)
	@NotBlank(message = "{user.email.not-empty}")
	@Column(nullable = false, unique = true)
	private String email;

	@ApiModelProperty(notes = "The password of the User.",
		example = "!Qazse4", required = true)
	@NotBlank(message = "{user.password.not-empty}")
	@Column(nullable = false)
	private String password;

	@ApiModelProperty(notes = "The first name of the User.",
		example = "John", required = true)
	@NotBlank(message = "{user.first-name.not-empty}")
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@ApiModelProperty(notes = "The last name of the User.",
		example = "Doe", required = true)
	@NotBlank(message = "{user.last-name.not-empty}")
	@Column(name = "last_name", nullable = false)
	private String lastName;

	private boolean enabled;

	public User() {
		super();
	}

	public User(@NotBlank(message = "{user.email.not-empty}") String email,
			@NotBlank(message = "{user.password.not-empty}") String password,
			@NotBlank(message = "{user.first-name.not-empty}") String firstName,
			@NotBlank(message = "{user.last-name.not-empty}") String lastName, boolean enabled) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.enabled = isEnabled;
	}

	@Override
	public String toString() {
		return email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

}
