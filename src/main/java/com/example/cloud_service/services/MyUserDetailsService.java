package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UserDAO;
import com.example.cloud_service.datamodel.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public MyUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserDAO userDAO = usersRepository.findFirstByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + login));
        return new User(userDAO.getLogin(), userDAO.getPasswordHash(), List.of());
    }

    public UserDetails loadUserBySignature(String token) throws UsernameNotFoundException {
        UserDAO userDAO = usersRepository.findFirstBySignature(token)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        return new User(userDAO.getLogin(), userDAO.getPasswordHash(), List.of());
    }
}