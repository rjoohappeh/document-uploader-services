package com.fdmgroup.documentuploader.documentuploaderservices.service;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.registration.ConfirmationToken;
import com.fdmgroup.documentuploader.model.registration.RegistrationWrapper;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.repository.ConfirmationTokenRepository;
import com.fdmgroup.documentuploader.service.account.AbstractAccountService;
import com.fdmgroup.documentuploader.service.authgroup.AbstractAuthGroupService;
import com.fdmgroup.documentuploader.service.register.RegisterService;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = { RegisterService.class })
class RegisterServiceTest {

	private static final String TOKEN = "token";
	
	@Mock
	private RegistrationWrapper mockRegistrationWrapper;
	
	@Mock
	private Account mockAccount;
	
	@Mock
	private User mockUser;
	
	@Mock
	private AuthGroup mockAuthGroup;
	
	@Mock
	private ConfirmationToken mockConfirmationToken;
	
	@MockBean
	private AbstractAccountService mockAccountService;
	
	@MockBean
	private AbstractUserService mockUserService;
	
	@MockBean
	private AbstractAuthGroupService mockAuthGroupService;
	
	@MockBean
	private ConfirmationTokenRepository mockConfirmationTokenRepository;
	
	@Autowired
	private RegisterService registerService;
	
	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void contextLoads() {
		
	}
	
	@Test
	void testProcessRegistration_callsUserAuthGroupAndAccountServiceSaveMethods() {
		when(mockRegistrationWrapper.getAccount()).thenReturn(mockAccount);
		when(mockRegistrationWrapper.getUser()).thenReturn(mockUser);
		when(mockRegistrationWrapper.getAuthGroup()).thenReturn(mockAuthGroup);
		when(mockUserService.save(mockUser)).thenReturn(mockUser);
		
		registerService.processRegistration(mockRegistrationWrapper);
		
		verify(mockUserService, times(1)).save(mockUser);
		verify(mockAuthGroupService, times(1)).save(mockAuthGroup);
		verify(mockAccountService, times(1)).save(mockAccount);
	}
	
	@Test
	void testProcessRegistration_returnsResultReturnedFromUserServiceSave() {
		when(mockRegistrationWrapper.getAccount()).thenReturn(mockAccount);
		when(mockRegistrationWrapper.getUser()).thenReturn(mockUser);
		when(mockRegistrationWrapper.getAuthGroup()).thenReturn(mockAuthGroup);
		when(mockUserService.save(mockUser)).thenReturn(mockUser);
		
		User actual = registerService.processRegistration(mockRegistrationWrapper);
		
		Assertions.assertEquals(mockUser, actual);
	}
	
	@Test
	void testActivateAccountWithToken_returnsFalse_whenTokenGivenDoesNotExist() {
		when(mockConfirmationTokenRepository.findByToken(TOKEN)).thenReturn(Optional.empty());
		
		boolean result = registerService.activateAccountWithToken(TOKEN);
		
		Assertions.assertFalse(result);
	}
	
	@Test
	void testActivateAccountWithToken_returnsFalse_whenTokenGivenIsExpired() {
		when(mockConfirmationTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(mockConfirmationToken));
		when(mockConfirmationToken.getExpiryDate()).thenReturn(DateUtil.yesterday());
		
		boolean result = registerService.activateAccountWithToken(TOKEN);
		
		Assertions.assertFalse(result);
	}
	
	@Test
	void testActivateAccountWithToken_callsUserServiceUpdate_whenTokenIsFoundAndIsNotExpired() {
		when(mockConfirmationTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(mockConfirmationToken));
		when(mockConfirmationToken.getExpiryDate()).thenReturn(DateUtil.parse("9999-12-25"));
		when(mockConfirmationToken.getUser()).thenReturn(mockUser);
		
		registerService.activateAccountWithToken(TOKEN);
		
		verify(mockUserService, times(1)).update(mockUser);
	}

	@Test
	void testActivateAccountWithToken_callConfirmationTokenRepositoryDelete_whenTokenIsFoundAndIsNotExpired() {
		when(mockConfirmationTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(mockConfirmationToken));
		when(mockConfirmationToken.getExpiryDate()).thenReturn(DateUtil.parse("9999-12-25"));
		when(mockConfirmationToken.getUser()).thenReturn(mockUser);

		registerService.activateAccountWithToken(TOKEN);

		verify(mockConfirmationTokenRepository, times(1)).delete(mockConfirmationToken);
	}
	
	@Test
	void testActivateAccountWithToken_returnsTrue_whenTokenIsFoundAndIsNotExpired() {
		when(mockConfirmationTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(mockConfirmationToken));
		when(mockConfirmationToken.getExpiryDate()).thenReturn(DateUtil.parse("9999-12-25"));
		when(mockConfirmationToken.getUser()).thenReturn(mockUser);
		
		boolean result = registerService.activateAccountWithToken(TOKEN);
		
		Assertions.assertTrue(result);
	}
}
