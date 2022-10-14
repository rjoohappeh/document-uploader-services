package com.fdmgroup.documentuploader.service.document;

import java.util.Optional;

import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.document.Document;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to the
 * modification, retrieval, addition, or removal of {@link Document} instances
 * in the system.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractDocumentService {

	/**
	 * Uploads the given {@link Document} object to the data storage system used.
	 * 
	 * @param document the {@code Document} to upload
	 * @return the updated {@code Document} instance to use for further operations
	 * @throws EntityCouldNotBeSavedException when the given {@code Document} could
	 *                                        not be saved for any reason
	 */
	Document uploadDocument(Document document);

	/**
	 * Retrieves a {@link Document} with an id matching the value of
	 * {@code documentId}.
	 * 
	 * @param documentId the document id to search for a {@code Document} object
	 *                   with
	 * @return {@code empty} {@link Optional} if no document is found with the given
	 *         {@code documentId}. Otherwise, an {@code Optional} encapsulating the
	 *         found {@code Document} object otherwise
	 */
	Optional<Document> getDocumentById(long documentId);

	/**
	 * Retrieves a {@link Document} with a name matching the value of
	 * {@code fileName}.
	 * 
	 * @param documentName the document name to search for a {@code Document} object
	 *                     with
	 * @return {@code empty} {@link Optional} if no document is found with the given
	 *         {@code documentName}. Otherwise, an {@code Optional} encapsulating
	 *         the found {@code Document} object otherwise
	 */
	Optional<Document> getDocumentByName(String documentName);

	/**
	 * Deletes the {@link Document} associated with the given {@code documentId}.
	 * 
	 * @param documentId the {@code id} of a {@code Document}
	 * @throws EntityNotFoundException when no {@code Document} exists with the
	 *                                 given {@code documentId}
	 */
	void deleteDocumentById(long documentId);
}
