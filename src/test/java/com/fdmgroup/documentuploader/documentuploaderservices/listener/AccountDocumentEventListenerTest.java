package com.fdmgroup.documentuploader.documentuploaderservices.listener;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.events.AccountDocumentEvent;
import com.fdmgroup.documentuploader.listener.AccountDocumentEventListener;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.email.AbstractEmailService;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

class AccountDocumentEventListenerTest {

    private static final String FILE_REMOVED_SUBJECT = "A File Has Been Removed From One Of Your Accounts";
    private static final String FILE_ADDED_SUBJECT = "A File Has Been Added To One Of Your Accounts";
    private static final String FILE_REMOVED_MESSAGE = "A file named 'null' has been removed from the account named 'null'" + Strings.LINE_SEPARATOR + "Click null/login to login to the application and view your account!";
    private static final String FILE_ADDED_MESSAGE = "A file named 'null' has been added to the account named 'null'" + Strings.LINE_SEPARATOR + "Click null/login to login to the application and view your account!";
    private static final String TEST_EMAIL = "testEmail@email.com";
    private AccountDocumentEventListener accountDocumentEventListener;

    @Mock
    private AbstractEmailService mockAbstractEmailService;
    @Mock
    private ApplicationProperties mockApplicationProperties;
    @Mock
    private AccountDocumentEvent mockAccountDocumentEvent;
    @Mock
    private Document mockDocument;
    @Mock
    private Account mockAccount;
    @Mock
    private User mockUser;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        accountDocumentEventListener = new AccountDocumentEventListener(mockAbstractEmailService, mockApplicationProperties);
        when(mockAccountDocumentEvent.getDocument()).thenReturn(mockDocument);
        when(mockAccountDocumentEvent.getAccount()).thenReturn(mockAccount);
    }

    @Test
    void testOnApplicationEvent_callsEventGetDocument() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAccountDocumentEvent).getDocument();
    }

    @Test
    void testOnApplicationEvent_callsEventGetAccount() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAccountDocumentEvent).getAccount();
    }

    @Test
    void testOnApplicationEvent_callsEventWasAddedToAccount() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAccountDocumentEvent).wasAddedToAccount();
    }

    @Test
    void testOnApplicationEvent_callsDocumentGetName() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockDocument).getName();
    }

    @Test
    void testOnApplicationEvent_callsAccountGetName() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAccount).getName();
    }

    @Test
    void testOnApplicationEvent_callsApplicationPropertiesGetHostUrl() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockApplicationProperties).getHostUrl();
    }

    @Test
    void testOnApplicationEvent_callsAccountGetUsers() {
        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAccount).getUsers();
    }

    @Test
    void testOnApplicationEvent_callsEmailServiceSendEmail_withRemovedSubjectAndMessage_whenWasAddedToAccountIsFalse() {
        Set<User> mockUsers = new HashSet<>(Arrays.asList(mockUser));
        when(mockAccount.getUsers()).thenReturn(mockUsers);
        when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAbstractEmailService).sendEmail(TEST_EMAIL, FILE_REMOVED_SUBJECT, FILE_REMOVED_MESSAGE);
    }

    @Test
    void testOnApplicationEvent_callsEmailServiceSendEmail_withAddedSubjectAndMessage_whenWasAddedToAccountIsTrue() {
        Set<User> mockUsers = new HashSet<>(Arrays.asList(mockUser));
        when(mockAccount.getUsers()).thenReturn(mockUsers);
        when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
        when(mockAccountDocumentEvent.wasAddedToAccount()).thenReturn(true);

        accountDocumentEventListener.onApplicationEvent(mockAccountDocumentEvent);

        verify(mockAbstractEmailService).sendEmail(TEST_EMAIL, FILE_ADDED_SUBJECT, FILE_ADDED_MESSAGE);
    }
}
