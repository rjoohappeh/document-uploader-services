package com.fdmgroup.documentuploader.repository;

import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository which performs CRUD database operations on {@link AuthGroup}
 * objects.
 * 
 * @author Noah Anderson
 */
@Repository
public interface AuthGroupRepository extends JpaRepository<AuthGroup, Long> {

	/**
	 * Retrieves all {@link AuthGroup AuthGroups} belonging to a {@link User} with a
	 * username equaling {@code username}
	 * 
	 * @param username the {@code username} to search for a {@code AuthGroup} object
	 *                 with the given {@code username}, <br/>
	 * @return {@link List} containing all found {@code AuthGroups} which belong to
	 *         the {@code user} with the given {@code username}
	 */
	List<AuthGroup> findByUsername(String username);
}
