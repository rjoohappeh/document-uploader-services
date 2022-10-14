package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.service.authgroup.AbstractAuthGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("${app.request-uris.auth-group}")
public class AuthGroupController {

	private final AbstractAuthGroupService authGroupService;

	@Autowired
	public AuthGroupController(AbstractAuthGroupService authGroupService) {
		super();
		this.authGroupService = authGroupService;
	}

	@ApiOperation(value = "Gets a list of AuthGroups which a User with the given username has", response = AuthGroup.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved AuthGroups")
	})
	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AuthGroup>> findAuthGroupsByUsername(@RequestParam(name = "username") String username) {
		List<AuthGroup> authGroups = authGroupService.getAuthGroupsByUsername(username);
		return ResponseEntity.ok(authGroups);
	}
}
