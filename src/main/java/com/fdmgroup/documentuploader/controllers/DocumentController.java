package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.document.AbstractDocumentService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@Api(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("${app.request-uris.documents}")
public class DocumentController {

	private final AbstractDocumentService documentService;

	@Autowired
	public DocumentController(AbstractDocumentService documentService) {
		super();
		this.documentService = documentService;
	}

	@ApiOperation(value = "Saves a new document to the database", response = Document.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully saved the document to the database"),
			@ApiResponse(code = 400, message = "Document may already exist or an invalid request body was given")
	})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Document> saveDocument(@Valid @RequestBody Document document) {
		Document savedDocument = documentService.uploadDocument(document);
		return ResponseEntity.created(URI.create(
				ServletUriComponentsBuilder.fromCurrentRequest().toUriString() + "/" + savedDocument.getId()))
				.body(savedDocument);
	}

	@ApiOperation(value = "Retrieves a document from the database", response = Document.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved a document from the database")
	})
	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Optional<Document>> getDocument(@RequestParam(value = "id", required = false) Long documentId,
										  @RequestParam(value = "documentName", required = false) String documentName) {
		Optional<Document> body;
		if (documentId != null) {
			body = documentService.getDocumentById(documentId);
		} else if (documentName != null) {
			body = documentService.getDocumentByName(documentName);
		} else {
			body = Optional.empty();
		}

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(body);
	}

	@ApiOperation(value = "Deletes an existing document from the database")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Successfully deleted the document"),
			@ApiResponse(code = 404, message = "No document was found with the given document id")
	})
	@DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> removeDocumentById(@PathVariable(value = "id") long documentId) {
		documentService.deleteDocumentById(documentId);
		return ResponseEntity.noContent().build();
	}
}
