package com.fdmgroup.documentuploader.model.user;

/**
 * Contains constant values which represent the various roles a {@link User} may
 * have. Users may have no more than one role at once. The {@link AuthGroup} class
 * binds roles together with Users.
 * 
 * @author Noah Anderson
 */
public enum Role {
	ROLE_USER, ROLE_ADMIN
}
