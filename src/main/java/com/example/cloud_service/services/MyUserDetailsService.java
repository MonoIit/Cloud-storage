package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UserDAO;
import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.model.MyUserDetails;
import com.example.cloud_service.model.ResourseNotFoundException;
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
    public UserDetails loadUserByUsername(String login) {
        UserDAO userDAO = usersRepository.findFirstByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + login));
        return new User(userDAO.getLogin(), userDAO.getPasswordHash(), List.of());
    }

    public UserDetails loadUserBySignature(String token) {
        UserDAO userDAO = usersRepository.findFirstBySignature(token)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        return new MyUserDetails(userDAO.getLogin(), userDAO.getPasswordHash(), userDAO.getId());
    }
}