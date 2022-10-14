package com.fdmgroup.documentuploader.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.documentuploader.model.document.Document;

/**
 * Repository which performs CRUD database operations on {@link Document}
 * objects.
 * 
 * @author Noah Anderson
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	/**
	 * Retrieves a {@link Document} with a name matching the value of
	 * {@code fileName}.
	 * 
	 * @param name the document name to search for a {@code Document} object with
	 * @return {@code empty} {@link Optional} if no document is found with the given
	 *         {@code name}, <br/>
	 *         {@code Optional} encapsulating the found {@code Document} object
	 *         otherwise
	 */
	Optional<Document> findByName(String name);

}
