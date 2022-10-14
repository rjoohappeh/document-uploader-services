package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Api(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RestController
@RequestMapping("${app.request-uris.users}")
public class UserController {

	private final AbstractUserService userService;

	@Autowired
	public UserController(AbstractUserService userService) {
		super();
		this.userService = userService;
	}

	@ApiOperation(value = "Retrieve an existing user", response = User.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved user") })
	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Optional<User>> getUser(@RequestParam(value = "email", required = false) String userEmail,
												   @RequestParam(value = "id", required = false) Long userId) {
		Optional<User> body;
		if (userEmail != null) {
			body = userService.getUserByEmail(userEmail);
		} else if (userId != null) {
			body = userService.getUserById(userId);
		} else {
			body = Optional.empty();
		}

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(body);
	}

	@ApiOperation(value = "Update an existing user", response = User.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User successfully updated"),
			@ApiResponse(code = 400, message = "User does not exist or invalid request body was given")
	})
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> update(@Valid @RequestBody User user) {
		User updatedUser = userService.update(user);
		return ResponseEntity.ok(updatedUser);
	}

	@ApiOperation(value = "Check if the account registered with the given email is enabled", response = Boolean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User exists and may or may not be enabled"),
			@ApiResponse(code = 400, message = "No user was found with the given email")
	})
	@GetMapping(value = "/{email}"
			+ "${app.request-uris.is-enabled}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isEnabled(@PathVariable("email") String userEmail) {
		boolean isEnabled = userService.isEnabledByEmail(userEmail);
		return ResponseEntity.ok(isEnabled);
	}

	@ApiOperation(value = "Sends an email to the provided email with instructions on how to reset their password")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Email was sent to the email given with instructions on how to reset their password"),
			@ApiResponse(code = 400, message = "No user was found with the given email")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping(value = "/{email}" + "${app.request-uris.reset-password}")
	public ResponseEntity<Void> resetPassword(@PathVariable("email") String userEmail) {
		userService.sendResetPasswordEmail(userEmail);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value = "Checks if the token provided is valid")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Token exists and may or may not be valid")
	})
	@GetMapping(value = "${app.request-uris.reset-password}" + "${app.request-uris.token}")
	public ResponseEntity<Boolean> isValidPasswordResetToken(@NotBlank(message = "{user.token.not-empty}") @RequestParam(value = "token") String passwordResetToken) {
		boolean isValidToken = userService.isValidPasswordResetToken(passwordResetToken);
		return ResponseEntity.ok(isValidToken);
	}

	@ApiOperation(value = "Resets the password of the account registered with the given email")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Password has been successfully reset"),
		@ApiResponse(code = 400, message = "Invalid email, password, or token provided or no User was found with the given email")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping(value = "${app.request-uris.reset-password}")
	public ResponseEntity<Void> changePassword(@Email(message = "{user.email.not-empty") @RequestParam(value = "email") String userEmail,
							   @NotBlank(message = "{user.password.not-empty}") @RequestParam(value = "newPassword") String newPassword,
							   @NotBlank(message = "{user.token.not-empty}") @RequestParam(value = "token") String passwordResetToken) {
		userService.changePassword(userEmail, newPassword, passwordResetToken);
		return ResponseEntity.noContent().build();
	}
}
