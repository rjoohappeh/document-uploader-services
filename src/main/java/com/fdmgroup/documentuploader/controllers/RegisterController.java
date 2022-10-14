package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.model.registration.RegistrationWrapper;
import com.fdmgroup.documentuploader.service.register.AbstractRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("${app.request-uris.register}")
public class RegisterController {

	private final AbstractRegisterService registerService;

	@Autowired
	public RegisterController(AbstractRegisterService registerService) {
		super();
		this.registerService = registerService;
	}

	@ApiOperation(value = "Registers a new account")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Account successfully created."),
			@ApiResponse(code = 400, message = "Invalid request body"),
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> registerNewUser(@Valid @RequestBody RegistrationWrapper wrapper) {
		registerService.processRegistration(wrapper);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value = "Activates a created account", response = Boolean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Account may or may not be activated")
	})
	@PatchMapping(value = "${app.request-uris.confirm-token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> confirmRegistration(@RequestBody String token) {
		boolean isActivated = registerService.activateAccountWithToken(token);
		return ResponseEntity.ok(isActivated);
	}

}
