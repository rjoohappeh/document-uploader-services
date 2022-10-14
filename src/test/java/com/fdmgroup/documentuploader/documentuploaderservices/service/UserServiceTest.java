package com.fdmgroup.documentuploader.documentuploaderservices.service;

import com.fdmgroup.documentuploader.config.BeanConfiguration;
import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;
import com.fdmgroup.documentuploader.exceptions.InvalidTokenException;
import com.fdmgroup.documentuploader.model.user.PasswordResetToken;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.repository.PasswordResetTokenRepository;
import com.fdmgroup.documentuploader.repository.UserRepository;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { UserService.class, BeanConfiguration.class })
class UserServiceTest {

	private static final String TEST_EMAIL = "test@email.com";
	private static final String TEST_EMAIL2 = "test2@email.com";
	private static final String TEST_PASSWORD = "password";
	private static final String TEST_TOKEN = "testToken123";

	@Mock
	private static User mockUser;

	@Mock
	private static PasswordResetToken mockPasswordResetToken;

	@MockBean
	private UserRepository mockUserRepository;

	@MockBean
	private PasswordResetTokenRepository mockPasswordResetTokenRepository;

	@MockBean
	private ApplicationEventPublisher mockEventPublisher;

	@Autowired
	private UserService userService;

	private static Stream<Optional<User>> getOptionalUsersForTests() {
		return Stream.of(Optional.empty(), Optional.of(mockUser));
	}

	private static Stream<Optional<PasswordResetToken>> getOptionalTokensForTests() {
		return Stream.of(Optional.empty(), Optional.of(mockPasswordResetToken));
	}

	private static Stream<Boolean> getBooleansForTest() {
		return Stream.of(true, false);
	}

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void contextLoads() {

	}

	@Test
	void testGetUserByEmail_callsUserRepositoryFindByEmail() {
		userService.getUserByEmail(TEST_EMAIL);

		verify(mockUserRepository, times(1)).findByEmail(TEST_EMAIL);
	}

	@ParameterizedTest
	@MethodSource("getOptionalUsersForTests")
	void testGetUserByEmail_returnsResultReturnedFromRepositoryFindByEmail(Optional<User> expected) {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(expected);

		Optional<User> actual = userService.getUserByEmail(TEST_EMAIL);

		assertEquals(expected, actual);
	}

	@Test
	void testGetUserById_callsUserRepositoryFindById() {
		userService.getUserById(0L);

		verify(mockUserRepository, times(1)).findById(0L);
	}

	@ParameterizedTest
	@MethodSource("getOptionalUsersForTests")
	void testGetUserById_returnsResultReturnedFromRepositoryFindById(Optional<User> expected) {
		when(mockUserRepository.findById(0L)).thenReturn(expected);

		Optional<User> actual = userService.getUserById(0L);

		assertEquals(expected, actual);
	}

	@Test
	void testSave_callsUserGetEmail() {
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.save(mockUser);

		verify(mockUser, times(1)).getEmail();
	}

	@Test
	void testSave_callsUserRepositoryExistsByEmail() {
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.save(mockUser);

		verify(mockUserRepository, times(1)).existsByEmail(TEST_EMAIL);
	}

	@Test
	void testSave_throwsEntityCouldNotBeSavedException_whenExistsByEmailReturnsTrue() {
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
		when(mockUserRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

		assertThrows(EntityCouldNotBeSavedException.class, () -> userService.save(mockUser));
	}

	@Test
	void testSave_callsUserRepositorySave_whenEmailDoesNotExist() {
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.save(mockUser);

		verify(mockUserRepository, times(1)).save(mockUser);
	}

	@Test
	void testUpdate_throwsEntityNotFoundException_whenEmailOfUserGivenDoesNotAlreadyExist() {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		assertThrows(EntityNotFoundException.class, () -> userService.update(mockUser));
	}

	@Test
	void testUpdate_callsUserSetPasswordAndRepositorySave_whenEmailOfUserGivenDoesExist()
			throws EntityNotFoundException {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
		when(mockUser.getPassword()).thenReturn(TEST_PASSWORD);
		userService.update(mockUser);

		verify(mockUser, times(1)).setPassword(TEST_PASSWORD);
		verify(mockUserRepository, times(1)).save(mockUser);
	}

	@Test
	void testUpdate_returnsResultReturnedFromRepositorySave_whenEmailOfUserGivenDoesExist()
			throws EntityNotFoundException {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
		when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
		when(mockUser.getPassword()).thenReturn(TEST_PASSWORD);

		User result = userService.update(mockUser);

		assertEquals(mockUser, result);
	}

	@Test
	void testIsEnabledByEmail_throwsEntityNotFoundException_whenUserEmailGivenDoesNotExist() {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.isEnabledByEmail(TEST_EMAIL));
	}

	@ParameterizedTest
	@MethodSource("getBooleansForTest")
	void testIsEnabledByEmail_returnsValueReturnedFromUserIsEnabled(boolean expected) throws EntityNotFoundException {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
		when(mockUser.isEnabled()).thenReturn(expected);

		boolean actual = userService.isEnabledByEmail(TEST_EMAIL);

		assertEquals(expected, actual);
	}

	@Test
	void testSendResetPasswordEmail_callsUserRepositoryFindByEmail() throws EntityNotFoundException {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

		userService.sendResetPasswordEmail(TEST_EMAIL);

		verify(mockUserRepository, times(1)).findByEmail(TEST_EMAIL);
	}

	@Test
	void testSendResetPasswordEmail_throwsEntityNotFoundException_whenEmailGivenDoesNotExist() {
		when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.sendResetPasswordEmail(TEST_EMAIL));
	}

	@Test
	void testIsValidPasswordResetToken_callsResetTokenRepositoryFindByToken() {
		userService.isValidPasswordResetToken(TEST_TOKEN);

		verify(mockPasswordResetTokenRepository, times(1)).findByToken(TEST_TOKEN);
	}

	@Test
	void testIsValidPasswordResetToken_returnsFalse_whenEmptyOptionalIsReturnedFromRepositoryFindByToken() {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.empty());

		boolean actual = userService.isValidPasswordResetToken(TEST_TOKEN);

		assertFalse(actual);
	}

	@Test
	void testIsValidPasswordResetToken_callsPasswordResetTokenIsExpired_whenTokenExists() {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));

		userService.isValidPasswordResetToken(TEST_TOKEN);

		verify(mockPasswordResetToken, times(1)).isExpired();
	}

	@Test
	void testIsValidPasswordResetToken_returnsOppositeOfResultFromIsExpired_whenTokenExists() {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.empty(),
				Optional.of(mockPasswordResetToken));

		when(mockPasswordResetToken.isExpired()).thenReturn(true).thenReturn(false);

		boolean actual1 = userService.isValidPasswordResetToken(TEST_TOKEN);
		boolean actual2 = userService.isValidPasswordResetToken(TEST_TOKEN);

		assertFalse(actual1);
		assertFalse(actual2);
	}

	@Test
	void testChangePassword_callsPasswordResetTokenRepositoryFindByToken()
			throws EntityNotFoundException, InvalidTokenException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockPasswordResetTokenRepository, times(1)).findByToken(TEST_TOKEN);
	}

	@Test
	void testChangePassword_throwsEntityNotFoundException_whenNoPasswordResetTokenIsFound() {
		assertThrows(EntityNotFoundException.class,
				() -> userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN));
	}

	@Test
	void testChangePassword_callsPasswordResetTokenIsExpired_whenTokenIsFound()
			throws EntityNotFoundException, InvalidTokenException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockPasswordResetToken, times(1)).isExpired();
	}

	@Test
	void testChangePassword_throwsExpiredPasswordToken_whenIsExpiredReturnsTrue() {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.isExpired()).thenReturn(true);
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);

		assertThrows(InvalidTokenException.class,
				() -> userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN));
	}

	@Test
	void testChangePassword_callsPasswordResetTokenGetUser_whenTokenIsFound()
			throws EntityNotFoundException, InvalidTokenException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockPasswordResetToken, times(1)).getUser();
	}

	@Test
	void testChangePassword_callsUserGetEmail_whenTokenIsFoundAndNotExpired()
			throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockUser, times(1)).getEmail();
	}

	@Test
	void testChangePassword_throwsInvalidTokenException_whenEmailOfUserOnTokenDoesNotMatchTokenGiven()
			throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL2);

		assertThrows(InvalidTokenException.class,
				() -> userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN));
	}

	@Test
	void testChangePassword_callsPasswordResetTokenIsUsed() throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockPasswordResetToken, times(1)).isUsed();
	}

	@Test
	void testChangePassword_throwsInvalidTokenException_whenIsUsedReturnsTrue() {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
		when(mockPasswordResetToken.isUsed()).thenReturn(true);

		assertThrows(InvalidTokenException.class,
				() -> userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN));
	}

	@Test
	void testChangePassword_callsUserSetPassword_whenEmailsMatch()
			throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockUser, times(1)).setPassword(TEST_PASSWORD);
	}

	@Test
	void testChangePassword_callsUserRepositorySave_whenNoExceptionsAreThrown()
			throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockUserRepository, times(1)).save(mockUser);
	}

	@Test
	void testChangePassword_callsPasswordResetTokenSetUsed() throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockPasswordResetToken, times(1)).setUsed(true);
	}

	@Test
	void testChangePassword_callsPasswordResetTokenRepositorySave()
			throws InvalidTokenException, EntityNotFoundException {
		when(mockPasswordResetTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(mockPasswordResetToken));
		when(mockPasswordResetToken.getUser()).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		userService.changePassword(TEST_EMAIL, TEST_PASSWORD, TEST_TOKEN);

		verify(mockPasswordResetTokenRepository, times(1)).save(mockPasswordResetToken);
	}
}
