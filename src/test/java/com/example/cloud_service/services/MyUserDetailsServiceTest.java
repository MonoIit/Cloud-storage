package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UserDAO;
import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.model.ResourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyUserDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_success() {
        UserDAO dao = new UserDAO();
        dao.setLogin("john");
        dao.setPasswordHash("hashed-pass");

        when(usersRepository.findFirstByLogin("john"))
                .thenReturn(Optional.of(dao));

        UserDetails userDetails = myUserDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("hashed-pass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_notFound_throwsException() {
        when(usersRepository.findFirstByLogin("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void loadUserBySignature_success() {
        UserDAO dao = new UserDAO();
        dao.setLogin("john");
        dao.setPasswordHash("hashed-pass");

        when(usersRepository.findFirstBySignature("token-123"))
                .thenReturn(Optional.of(dao));

        UserDetails userDetails = myUserDetailsService.loadUserBySignature("token-123");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("hashed-pass", userDetails.getPassword());
    }

    @Test
    void loadUserBySignature_notFound_throwsException() {
        when(usersRepository.findFirstBySignature("bad-token"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserBySignature("bad-token"));
    }
}