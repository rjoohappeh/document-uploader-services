package com.fdmgroup.documentuploader.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Encapsulates information related to the permissions any {@link User} of the
 * system may have.
 * 
 * @author Noah Anderson
 */
@ApiModel(description = "Model which contains both the username of a user and their associated role.")
@Entity
@Table(name = "AUTH_GROUP")
public class AuthGroup {

	@ApiModelProperty(notes = "Unique identifier of the AuthGroup.",
			example = "1", required = true)
	@Id
	@GeneratedValue(generator = "AUTH_GROUP_ID_GENERATOR", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "AUTH_GROUP_ID_GENERATOR", sequenceName = "AUTH_GROUP_ID_SEQUENCE", allocationSize = 1)
	private long id;

	@ApiModelProperty(notes = "The username of the AuthGroup. Must be in the format of an email.",
		example = "email@gmail.com", required = true)
	@Column(name = "username", nullable = false)
	private String username;

	@ApiModelProperty(notes = "The role associated with the AuthGroup.", required = true)
	@Column(name = "AUTH_GROUP", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	public AuthGroup() {
		super();
	}

	public AuthGroup(AuthGroupBuilder builder) {
		super();
		this.username = builder.username;
		this.role = builder.role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return username;
	}

	public void setEmail(String email) {
		this.username = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public static class AuthGroupBuilder {

		private String username;
		private Role role;

		public AuthGroupBuilder setUsername(String username) {
			this.username = username;
			return this;
		}

		public AuthGroupBuilder setRole(Role role) {
			this.role = role;
			return this;
		}

		public AuthGroup build() {
			return new AuthGroup(this);
		}
	}
}
