package com.fdmgroup.documentuploader.documentuploaderservices.service;

import com.fdmgroup.documentuploader.config.BeanConfiguration;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.repository.AccountRepository;
import com.fdmgroup.documentuploader.service.account.AccountService;
import com.fdmgroup.documentuploader.service.document.DocumentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { AccountService.class, BeanConfiguration.class })
@TestPropertySource(value = { "classpath:/application.properties"})
class AccountServiceTest {

	private static final String TEST_ACCOUNT_NAME = "accountTest";
	private static final String TEST_DOCUMENT_NAME = "documentTest";

	@Mock
	private Document mockDocument;

	@Mock
	private Account mockAccount;

	@MockBean
	private AccountRepository mockAccountRepository;

	@MockBean
	private DocumentService mockDocumentService;

	@Autowired
	private AccountService accountService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void contextLoads() {

	}

	@Test
	void testSave_throwsEntityCouldNotBeSavedException_ifAccountNameOfGivenAccountDoesExist() {
		when(mockAccount.getName()).thenReturn(TEST_ACCOUNT_NAME);
		when(mockAccountRepository.existsByName(TEST_ACCOUNT_NAME)).thenReturn(true);

		Assertions.assertThrows(EntityCouldNotBeSavedException.class, () -> accountService.save(mockAccount));
	}

	@Test
	void testSave_callsAccountRepositorySave_ifAccountNameOfGivenAccountDoesNotExist()
			throws EntityCouldNotBeSavedException {
		when(mockAccount.getName()).thenReturn(TEST_ACCOUNT_NAME);

		accountService.save(mockAccount);

		verify(mockAccountRepository, times(1)).save(mockAccount);
	}

	@Test
	void testSave_returnsAccountReturned_fromAccountRepositorySave_ifAccountNameOfGivenAccountDoesNotExist()
			throws EntityCouldNotBeSavedException {
		when(mockAccount.getName()).thenReturn(TEST_ACCOUNT_NAME);
		when(mockAccountRepository.save(mockAccount)).thenReturn(mockAccount);

		Account actual = accountService.save(mockAccount);

		Assertions.assertEquals(mockAccount, actual);
	}

	@Test
	void testUpdate_EntityNotFoundException_ifAccountDoesNotExist() {
		when(mockAccountRepository.existsById(anyLong())).thenReturn(false);

		Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.update(mockAccount));
	}

	@Test
	void testUpdate_callsAccountRepositorySave_ifAccountDoesExist() throws EntityNotFoundException {
		when(mockAccountRepository.existsById(anyLong())).thenReturn(true);

		accountService.update(mockAccount);

		verify(mockAccountRepository, times(1)).save(mockAccount);
	}

	@Test
	void testUpdate_returnsAccountReturned_fromAccountRepositorySave_ifAccountDoesExist()
			throws EntityNotFoundException {
		when(mockAccountRepository.existsById(anyLong())).thenReturn(true);
		when(mockAccountRepository.save(mockAccount)).thenReturn(mockAccount);

		Account actual = accountService.update(mockAccount);

		Assertions.assertEquals(mockAccount, actual);
	}

	@Test
	void testGetAccountById_callsAccountRepositoryFindById() {
		accountService.getAccountById(0L);

		verify(mockAccountRepository, times(1)).findById(0L);
	}

	@Test
	void testGetAccountById_returnsResultFromAccountRepositoryFindById() {
		when(mockAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

		Optional<Account> actual = accountService.getAccountById(0L);

		Assertions.assertEquals(Optional.empty(), actual);
	}

	@Test
	void testGetAccountsByUserId_callsAccountRepositoryFindAccountsByUserId() {
		accountService.getAccountsByUserId(0L);

		verify(mockAccountRepository, times(1)).findAccountsByUserId(0L);
	}

	@Test
	void testGetAccountsByUserId_returnsResultFromAccountRepositoryFindAccountsByUserId() {
		when(mockAccountRepository.findAccountsByUserId(anyLong())).thenReturn(Collections.emptyList());
		List<Account> actual = accountService.getAccountsByUserId(0L);

		Assertions.assertEquals(Collections.emptyList(), actual);
	}

	@Test
	void testGetAccountByOwnerId_callsAccountRepositoryFindByOwnerId() {
		accountService.getAccountByOwnerId(0L);

		verify(mockAccountRepository, times(1)).findByOwnerId(0L);
	}

	@Test
	void testGetAccountById_returnsResultFromAccountRepositoryFindByOwnerId() {
		when(mockAccountRepository.findByOwnerId(anyLong())).thenReturn(Optional.empty());
		Optional<Account> actual = accountService.getAccountByOwnerId(0L);

		Assertions.assertEquals(Optional.empty(), actual);
	}

	@Test
	void testGetAccountByName_callsAccountRepositoryFindByName() {
		accountService.getAccountByName(TEST_ACCOUNT_NAME);

		verify(mockAccountRepository, times(1)).findByName(TEST_ACCOUNT_NAME);
	}

	@Test
	void testGetAccountByName_returnsResultFromAccountRepositoryFindByName() {
		when(mockAccountRepository.findByName(TEST_ACCOUNT_NAME)).thenReturn(Optional.empty());

		Optional<Account> actual = accountService.getAccountByName(TEST_ACCOUNT_NAME);

		Assertions.assertEquals(Optional.empty(), actual);
	}

	@Test
	void testAddDocumentToAccountById_callsAccountRepositoryFindById()
			throws EntityNotFoundException, EntityCouldNotBeSavedException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

		accountService.addDocumentToAccountByAccountId(mockDocument, 1L);

		verify(mockAccountRepository, times(1)).findById(1L);
	}

	@Test
	void testAddDocumentToAccountById_throwsEntityNotFoundException_whenFindByIdReturnsEmptyOptional() {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.empty());

		Assertions.assertThrows(EntityNotFoundException.class,
				() -> accountService.addDocumentToAccountByAccountId(mockDocument, 1L));
	}

	@Test
	void testAddDocumentToAccountById_callsMockAccountGetDocument_whenAccountExists()
			throws EntityNotFoundException, EntityCouldNotBeSavedException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

		accountService.addDocumentToAccountByAccountId(mockDocument, 1L);

		verify(mockAccount, times(1)).getDocuments();
	}

	@Test
	void testAddDocumentToAccountById_throwsEntityCountNotBeSavedException_whenDocumentIsAlreadyOnTheAccount() {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		HashSet<Document> testDocuments = new HashSet<>();
		testDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(testDocuments);
		when(mockDocument.getName()).thenReturn("name");

		Assertions.assertThrows(EntityCouldNotBeSavedException.class,
				() -> accountService.addDocumentToAccountByAccountId(mockDocument, 1L));
	}

	@Test
	void testAddDocumentToAccountById_callsDocumentServiceUploadDocument_whenNoExceptionsAreThrown()
			throws EntityNotFoundException, EntityCouldNotBeSavedException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockAccount.getDocuments()).thenReturn(new HashSet<>());

		accountService.addDocumentToAccountByAccountId(mockDocument, 1L);

		verify(mockDocumentService, times(1)).uploadDocument(mockDocument);
	}

	@Test
	void testAddDocumentToAccountById_throwsEntityCouldNotBeSavedException_whenThrownByDocumentService()
			throws EntityCouldNotBeSavedException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.uploadDocument(mockDocument)).thenThrow(EntityCouldNotBeSavedException.class);

		Assertions.assertThrows(EntityCouldNotBeSavedException.class,
				() -> accountService.addDocumentToAccountByAccountId(mockDocument, 1L));
	}

	@Test
	void testAddDocumentToAccountById_callsAccountSetDocumentsWithSetContainingDocument()
			throws EntityNotFoundException, EntityCouldNotBeSavedException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockAccount.getDocuments()).thenReturn(new HashSet<>());
		when(mockDocumentService.uploadDocument(mockDocument)).thenReturn(mockDocument);

		accountService.addDocumentToAccountByAccountId(mockDocument, 1L);

		Set<Document> expectedDocuments = new HashSet<>();
		expectedDocuments.add(mockDocument);

		verify(mockAccount, times(1)).setDocuments(expectedDocuments);
	}

	@Test
	void testAddDocumentToAccountById_callsAccountRepositorySave_whenNoExceptionsAreThrown()
			throws EntityNotFoundException, EntityCouldNotBeSavedException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockAccount.getDocuments()).thenReturn(new HashSet<>());

		accountService.addDocumentToAccountByAccountId(mockDocument, 1L);

		verify(mockAccountRepository, times(1)).save(mockAccount);
	}

	@Test
	void testAddDocumentToAccountById_returnsAccountWithSetOfDocumentsContainingTheAddedDocument()
			throws EntityCouldNotBeSavedException, EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockAccount.getDocuments()).thenReturn(new HashSet<>());
		when(mockDocumentService.uploadDocument(mockDocument)).thenReturn(mockDocument);
		when(mockAccountRepository.save(mockAccount)).thenReturn(mockAccount);

		Account actual = accountService.addDocumentToAccountByAccountId(mockDocument, 1L);

		Assertions.assertEquals(mockAccount, actual);
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_callsAccountRepositoryFindById() throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		accountDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);

		accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L);

		verify(mockAccountRepository, times(1)).findById(1L);
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_throwsEntityNotFoundException_whenNoAccountIsFoundWithGivenId() {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.empty());

		Assertions.assertThrows(EntityNotFoundException.class,
				() -> accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L));
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_callsDocumentServiceGetDocumentByName()
			throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		accountDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);

		accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L);
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_throwsEntityNotFoundException_whenNoDocumentIsFoundWithGivenFileName() {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.empty());

		Assertions.assertThrows(EntityNotFoundException.class,
				() -> accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L));
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_callsDocumentServiceRemoveDocumentById()
			throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		accountDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);

		accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L);

		verify(mockDocumentService, times(1)).deleteDocumentById(mockDocument.getId());
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_callsAccountSetDocuments() throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		accountDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);

		accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L);

		accountDocuments.remove(mockDocument);
		verify(mockAccount, times(1)).setDocuments(accountDocuments);
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_callsAccountRepositorySave() throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		accountDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);

		accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L);

		accountDocuments.remove(mockDocument);
		verify(mockAccountRepository, times(1)).save(mockAccount);
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_throwsEntityNotFoundException_whenNoDocumentIsOnTheAccountWithTheGivenFileName()
			throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);

		Assertions.assertThrows(EntityNotFoundException.class,
				() -> accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L));
	}

	@Test
	void testRemoveDocumentFromAccountByFileName_returnsResultFromAccountRepositorySave_whenNoExceptionsAreThrown()
			throws EntityNotFoundException {
		when(mockAccountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
		when(mockDocumentService.getDocumentByName(TEST_DOCUMENT_NAME)).thenReturn(Optional.of(mockDocument));
		Set<Document> accountDocuments = new HashSet<>();
		accountDocuments.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		when(mockDocument.getName()).thenReturn(TEST_DOCUMENT_NAME);
		when(mockAccountRepository.save(mockAccount)).thenReturn(mockAccount);

		Account result = accountService.removeDocumentFromAccountByFileName(TEST_DOCUMENT_NAME, 1L);

		Assertions.assertEquals(mockAccount, result);
	}
}
