package com.fdmgroup.documentuploader.documentuploaderservices.service;

import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.repository.AuthGroupRepository;
import com.fdmgroup.documentuploader.service.authgroup.AuthGroupService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { AuthGroupService.class} )
class AuthGroupServiceTest {

	private static final String TEST_USERNAME = "username";

	@Mock
	private static AuthGroup mockAuthGroup;

	@MockBean
	private AuthGroupRepository mockAuthGroupRepository;

	@Autowired
	private AuthGroupService authGroupService;

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void contextLoads() {

	}

	@Test
	void testGetAuthGroupsByUsername_callsAuthGroupRepositoryFindByUsername() {
		authGroupService.getAuthGroupsByUsername(TEST_USERNAME);

		verify(mockAuthGroupRepository, times(1)).findByUsername(TEST_USERNAME);
	}

	@ParameterizedTest
	@MethodSource("getAuthGroupsForTests")
	void testGetAuthGroupsByUsername_returnsResultReturnedFromRepositoryFindByUsername(List<AuthGroup> expected) {
		when(mockAuthGroupRepository.findByUsername(TEST_USERNAME)).thenReturn(expected);

		List<AuthGroup> actual = authGroupService.getAuthGroupsByUsername(TEST_USERNAME);

		assertEquals(expected, actual);
	}

	private static Stream<List<AuthGroup>> getAuthGroupsForTests() {
		return Stream.of(
				Collections.emptyList(),
				Lists.list(mockAuthGroup)
		);
	}

	@Test
	void testSave_calledAuthGroupRepositorySave() {
		authGroupService.save(mockAuthGroup);

		verify(mockAuthGroupRepository, times(1)).save(mockAuthGroup);
	}

	@Test
	void testSave_returnsResultFromAuthGroupRepositorySave() {
		when(mockAuthGroupRepository.save(mockAuthGroup)).thenReturn(mockAuthGroup);

		AuthGroup result = authGroupService.save(mockAuthGroup);

		assertEquals(mockAuthGroup, result);
	}
}
