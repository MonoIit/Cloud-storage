package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.model.ConflictDataException;
import com.example.cloud_service.model.UserCreds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewUser_success() {
        UserCreds creds = new UserCreds("john", "password");

        when(passwordEncoder.encode("password")).thenReturn("encoded-pass");

        userService.addNewUser(creds);

        verify(usersRepository).save(argThat(user ->
                user.getLogin().equals("john") &&
                        user.getPasswordHash().equals("encoded-pass")));
    }

    @Test
    void addNewUser_conflict_throwsException() {
        UserCreds creds = new UserCreds("john", "password");

        when(passwordEncoder.encode("password")).thenReturn("encoded-pass");
        when(usersRepository.save(any()))
                .thenThrow(new ConflictDataException("duplicate"));

        assertThrows(ConflictDataException.class,
                () -> userService.addNewUser(creds));
    }

    @Test
    void deleteAccount_success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john");

        userService.deleteAccount(userDetails);

        verify(usersRepository).deleteByLogin("john");
    }
}
