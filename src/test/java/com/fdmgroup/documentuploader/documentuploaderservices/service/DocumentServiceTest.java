package com.fdmgroup.documentuploader.documentuploaderservices.service;

import com.fdmgroup.documentuploader.config.BeanConfiguration;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.repository.DocumentRepository;
import com.fdmgroup.documentuploader.service.document.DocumentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = { DocumentService.class, BeanConfiguration.class })
class DocumentServiceTest {

	private static final String TEST_DOCUMENT_NAME = "testDocumentName";
	
	@Mock
	private static Document mockDocument;

	@MockBean
	private DocumentRepository mockDocumentRepository;

	@Autowired
	private DocumentService documentService;

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void contextLoads() {

	}

	@Test
	void testUploadDocument_callsDocumentRepositoryExistsById() throws EntityCouldNotBeSavedException {
		documentService.uploadDocument(mockDocument);

		verify(mockDocumentRepository, times(1)).existsById(0L);
	}

	@Test
	void testUploadDocument_throwsEntityCouldNotBeSavedException_whenDocumentAlreadyExists() {
		when(mockDocumentRepository.existsById(0L)).thenReturn(true);

		Assertions.assertThrows(EntityCouldNotBeSavedException.class, () -> documentService.uploadDocument(mockDocument));
	}

	@Test
	void testUploadDocument_callsDocumentRepositorySave_whenDocumentDoesNotExist() throws EntityCouldNotBeSavedException {
		when(mockDocumentRepository.existsById(0L)).thenReturn(false);

		documentService.uploadDocument(mockDocument);
		verify(mockDocumentRepository, times(1)).save(mockDocument);
	}

	@Test
	void testUploadDocument_returnsResultReturnedFromDocumentRepositorySave_whenDocumentDoesNotExist() throws EntityCouldNotBeSavedException {
		when(mockDocumentRepository.existsById(0L)).thenReturn(false);
		when(mockDocumentRepository.save(mockDocument)).thenReturn(mockDocument);
		
		Document actual = documentService.uploadDocument(mockDocument);
		
		Assertions.assertEquals(mockDocument, actual);
	}
	
	@Test
	void testGetDocumentById_callsDocumentRepositoryFindById() {
		documentService.getDocumentById(0L);
		
		verify(mockDocumentRepository, times(1)).findById(0L);
	}
	
	@ParameterizedTest
	@MethodSource("provideOptionalsForTests")
	void testGetDocumentById_returnsResultReturnedFromDocumentRepositoryFindById(Optional<Document> expected) {
		when(mockDocumentRepository.findById(0L)).thenReturn(expected);
		
		Optional<Document> actual = documentService.getDocumentById(0L);
		
		Assertions.assertEquals(expected, actual);
	}

	private static Stream<Optional<Document>> provideOptionalsForTests() {
		return Stream.of(
				Optional.empty(),
				Optional.of(mockDocument)
		);
	}

	@Test
	void testGetDocumentByName_callsDocumentRepositoryFindByName() {
		documentService.getDocumentByName(TEST_DOCUMENT_NAME);
		
		verify(mockDocumentRepository, times(1)).findByName(TEST_DOCUMENT_NAME);
	}

	@ParameterizedTest
	@MethodSource("provideOptionalsForTests")
	void testGetDocumentBNameId_returnsResultReturnedFromDocumentRepositoryFindById(Optional<Document> expected) {
		when(mockDocumentRepository.findByName(TEST_DOCUMENT_NAME)).thenReturn(expected);
		
		Optional<Document> actual = documentService.getDocumentByName(TEST_DOCUMENT_NAME);

		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	void testDeleteDocumentById_callsDocumentRepositoryExistsById() throws EntityNotFoundException {
		when(mockDocumentRepository.existsById(0L)).thenReturn(true);
		
		documentService.deleteDocumentById(0L);

		verify(mockDocumentRepository, times(1)).existsById(0L);
	}
	
	@Test
	void testDeleteDocumentById_throwsEntityNotFoundException_whenExistsByIdReturnsFalse() {
		when(mockDocumentRepository.existsById(0L)).thenReturn(false);
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> documentService.deleteDocumentById(0L));
	}
	
	@Test
	void testDeleteDocumentById_callsDocumentRepositoryDeleteById_whenExistsByIdReturnsTrue() throws EntityNotFoundException {
		when(mockDocumentRepository.existsById(0L)).thenReturn(true);
		
		documentService.deleteDocumentById(0L);
		
		verify(mockDocumentRepository, times(1)).deleteById(0L);
	}
}
