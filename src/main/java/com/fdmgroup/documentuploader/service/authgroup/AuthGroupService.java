package com.fdmgroup.documentuploader.service.authgroup;

import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.repository.AuthGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Implementing class of {@link AbstractAuthGroupService} which performs
 * operations related to {@link AuthGroup} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class AuthGroupService implements AbstractAuthGroupService {

	private final AuthGroupRepository authGroupRepository;
	
	@Autowired
	public AuthGroupService(AuthGroupRepository authGroupRepository) {
		super();
		this.authGroupRepository = authGroupRepository;
	}

	@Override
	public List<AuthGroup> getAuthGroupsByUsername(String username) {
		return authGroupRepository.findByUsername(username);
	}

	@Override
	public AuthGroup save(AuthGroup authGroup) {
		return authGroupRepository.save(authGroup);
	}
}
