package com.fdmgroup.documentuploader.service.authgroup;

import com.fdmgroup.documentuploader.model.user.AuthGroup;

import java.util.List;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to operations
 * dealing with {@link AuthGroup} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractAuthGroupService {

	/**
	 * Retrieves a @{@link List} of {@link AuthGroup} objects with a username
	 * equaling {@code username}
	 * 
	 * @param username the {@code username} to search for a {@code AuthGroup} object
	 *                 with the given {@code username}. Otherwise an
	 *                 {@code Optional} encapsulating the found {@code AuthGroup}
	 *                 object.
	 */
	List<AuthGroup> getAuthGroupsByUsername(String username);

	/**
	 * Saves the given {@link AuthGroup} to the data source.
	 *
	 * @param authGroup the {@code AuthGroup} to be saved
	 * @return the saved {@code AuthGroup} instance
	 */
	AuthGroup save(AuthGroup authGroup);
}
