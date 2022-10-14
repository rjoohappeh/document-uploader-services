package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.account.AbstractAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Api(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("${app.request-uris.accounts}")
public class AccountController {

	private final AbstractAccountService accountService;

	@Autowired
	public AccountController(AbstractAccountService accountService) {
		super();
		this.accountService = accountService;
	}

	@ApiOperation(value = "Create a new account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully created new account"),
			@ApiResponse(code = 400, message = "Invalid request body was given")
	})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> saveAccount(@Valid @RequestBody Account account) {
		Account savedAccount = accountService.save(account);
		return ResponseEntity
				.created(URI.create(
						ServletUriComponentsBuilder.fromCurrentRequest().toUriString() + "/" + savedAccount.getId()))
				.body(savedAccount);
	}

	@ApiOperation(value = "Update an existing account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully updated an existing account"),
			@ApiResponse(code = 400, message = "Invalid request body was given")
	})
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> updateAccount(@Valid @RequestBody Account account) {
		Account updatedAccount = accountService.update(account);
		return ResponseEntity.ok(updatedAccount);
	}

	@ApiOperation(value = "Retrieve an existing account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved account(s)")
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAccount(@RequestParam(value = "id", required = false) Long accountId,
			@RequestParam(value = "name", required = false) String accountName,
			@RequestParam(value = "ownerId", required = false) Long ownerId,
			@RequestParam(value = "userId", required = false) Long userId) {
		Object body;
		if (accountId != null) {
			body = accountService.getAccountById(accountId);
		} else if (accountName != null) {
			body = accountService.getAccountByName(accountName);
		} else if (ownerId != null) {
			body = accountService.getAccountByOwnerId(ownerId);
		} else if (userId != null) {
			body = accountService.getAccountsByUserId(userId);
		} else {
			body = null;
		}

		return ResponseEntity.ok(body);
	}

	@ApiOperation(value = "Add a new document to an existing account")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully added a document to an account with the given account id"),
			@ApiResponse(code = 400, message = "Invalid request body"),
			@ApiResponse(code = 404, message = "No account exists with the given accoun iId")
	})
	@PutMapping(value = "/{id}"
			+ "${app.request-uris.documents}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> addDocumentToAccount(@Valid @RequestBody Document document,
			@PathVariable("id") long accountId) {
		Account accountWithDocumentAdded = accountService.addDocumentToAccountByAccountId(document, accountId);
		return ResponseEntity.ok(accountWithDocumentAdded);
	}

	@ApiOperation(value = "Remove a document from an existing account")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully removed a document from an account with the given account id"),
			@ApiResponse(code = 404, message = "No account was found with the given account id or no document was found "
					+ "with the given name on the found account")
	})
	@DeleteMapping(value = "/{id}" + "${app.request-uris.documents}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> removeDocumentFromAccount(@RequestParam("documentName") String documentName,
			@PathVariable("id") long accountId) {
		Account accountWithDocumentRemoved = accountService.removeDocumentFromAccountByFileName(documentName,
				accountId);
		return ResponseEntity.ok(accountWithDocumentRemoved);
	}
}
