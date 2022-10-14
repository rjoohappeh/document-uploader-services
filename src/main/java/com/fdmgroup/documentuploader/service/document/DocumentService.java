package com.fdmgroup.documentuploader.service.document;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.repository.DocumentRepository;

/**
 * <p>
 * Implementing class of {@link AbstractDocumentService} which performs
 * operations related to {@link Document} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class DocumentService implements AbstractDocumentService {

	private static final String DOCUMENT_ID = "id";

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;
	private final DocumentRepository documentRepository;
	
	@Autowired
	public DocumentService(MessageSource messageSource, DocumentRepository documentRepository) {
		super();
		this.messageSource = messageSource;
		this.documentRepository = documentRepository;
	}

	@Override
	public Document uploadDocument(Document document) {
		long documentId = document.getId();
		boolean documentExists = documentRepository.existsById(documentId);
		if (documentExists) {
			throw new EntityCouldNotBeSavedException(Document.class,
					messageSource.getMessage("document.exists", null, Locale.getDefault()));
		}
		return documentRepository.save(document);
	}

	@Override
	public Optional<Document> getDocumentById(long documentId) {
		return documentRepository.findById(documentId);
	}

	@Override
	public Optional<Document> getDocumentByName(String documentName) {
		return documentRepository.findByName(documentName);
	}

	@Override
	public void deleteDocumentById(long documentId) {
		boolean documentExists = documentRepository.existsById(documentId);
		if (!documentExists) {
			throw new EntityNotFoundException(Document.class, DOCUMENT_ID, String.valueOf(documentId));
		}
		documentRepository.deleteById(documentId);
	}

}
