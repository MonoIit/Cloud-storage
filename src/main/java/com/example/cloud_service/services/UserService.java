package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.datamodel.UserDAO;

import com.example.cloud_service.model.ConflictDataException;
import com.example.cloud_service.model.UserCreds;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addNewUser(UserCreds userCreds) throws ConflictDataException {
        try {
            usersRepository.save(new UserDAO(userCreds.getLogin(), passwordEncoder.encode(userCreds.getPassword())));

        } catch (DataIntegrityViolationException exception) {
            throw new ConflictDataException("You cannot use " + userCreds.getLogin() + exception);
        }
    }

    public void deleteAccount(UserDetails userDetails) {
        usersRepository.deleteByLogin(userDetails.getUsername());
    }
}
