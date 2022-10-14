package com.fdmgroup.documentuploader.model.registration;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Encapsulates objects required to create a new account within the system.
 * 
 * @author Noah Anderson
 *
 */
@ApiModel(description = "Wrapper class used for registering a new user.")
public class RegistrationWrapper {

	@ApiModelProperty(notes = "The User being registered.", required = true)
	@Valid
	@NotNull(message = "{registration-wrapper.user.not-empty}")
	private User user;

	@ApiModelProperty(notes = "The Account being created.", required = true)
	@Valid
	@NotNull(message = "{registration-wrapper.account.not-empty}")
	private Account account;

	@ApiModelProperty(notes = "The AuthGroup associated with the created User.", required = true)
	@Valid
	@NotNull(message = "{registration-wrapper.auth-group.not-empty}")
	private AuthGroup authGroup;

	public RegistrationWrapper() {
		super();
	}

	public RegistrationWrapper(@Valid @NotNull(message = "{registration-wrapper.user.not-empty}") User user,
			@Valid @NotNull(message = "{registration-wrapper.account.not-empty}") Account account,
			@Valid @NotNull(message = "{registration-wrapper.auth-group.not-empty}") AuthGroup authGroup) {
		this.user = user;
		this.account = account;
		this.authGroup = authGroup;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AuthGroup getAuthGroup() {
		return authGroup;
	}

	public void setAuthGroup(AuthGroup authGroup) {
		this.authGroup = authGroup;
	}

}
