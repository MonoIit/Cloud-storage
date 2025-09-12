package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UserDAO;
import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.model.AuthOkResponse;
import com.example.cloud_service.model.UserCreds;
import com.example.cloud_service.security.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TokenUtil tokenUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_success_createsNewSignature() throws Exception {
        UserCreds creds = new UserCreds("john", "");
        UserDAO userDAO = new UserDAO();
        userDAO.setLogin("john");

        when(usersRepository.findFirstByLogin("john"))
                .thenReturn(Optional.of(userDAO));
        when(tokenUtil.generateToken())
                .thenReturn("tokenPart.signature");

        AuthOkResponse response = authService.authenticate(creds);

        assertNotNull(response);
        assertEquals("tokenPart", response.getToken());

        ArgumentCaptor<UserDAO> captor = ArgumentCaptor.forClass(UserDAO.class);
        verify(usersRepository).save(captor.capture());
        assertEquals("signature", captor.getValue().getSignature());
    }

//    @Test
//    void authenticate_success_usesExistingSignature() throws Exception {
//        UserCreds creds = new UserCreds("john", "");
//        UserDAO userDAO = new UserDAO();
//        userDAO.setLogin("john");
//        userDAO.setSignature("existing-signature");
//
//        when(usersRepository.findFirstByLogin("john"))
//                .thenReturn(Optional.of(userDAO));
//
//        AuthOkResponse response = authService.authenticate(creds);
//
//        assertEquals("existing-signature", response.getToken());
//        verify(usersRepository, never()).save(any());
//    }

    @Test
    void authenticate_userNotFound_throwsException() {
        UserCreds creds = new UserCreds("unknown", "password");

        when(usersRepository.findFirstByLogin("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authService.authenticate(creds));
    }

    @Test
    void authenticate_disabledUser_throwsException() {
        UserCreds creds = new UserCreds("john", "password");

        doThrow(new DisabledException("disabled"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(DisabledException.class, () -> authService.authenticate(creds));
    }

    @Test
    void authenticate_invalidCredentials_throwsException() {
        UserCreds creds = new UserCreds("john", "wrong");

        doThrow(new BadCredentialsException("bad creds"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(creds));
    }

    @Test
    void logoutUser_success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john");

        UserDAO userDAO = new UserDAO();
        userDAO.setLogin("john");
        userDAO.setSignature("old-signature");

        when(usersRepository.findFirstByLogin("john"))
                .thenReturn(Optional.of(userDAO));

        authService.logoutUser(userDetails);

        ArgumentCaptor<UserDAO> captor = ArgumentCaptor.forClass(UserDAO.class);
        verify(usersRepository).save(captor.capture());
        assertNull(captor.getValue().getSignature());
    }

    @Test
    void logoutUser_userNotFound_throwsException() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("unknown");

        when(usersRepository.findFirstByLogin("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authService.logoutUser(userDetails));
    }
}

