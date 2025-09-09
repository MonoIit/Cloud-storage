package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.datamodel.UserDAO;

import com.example.cloud_service.model.ConflictDataException;
import com.example.cloud_service.model.UserCreds;
import jakarta.transaction.Transactional;
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

    public void addNewUser(UserCreds userCreds) {
        UserDAO userDAO = usersRepository.findFirstByLogin(userCreds.getLogin())
                .orElse(null);
        if (userDAO != null) {
            throw new ConflictDataException("Пользователь с этим логином уже существует");
        }
        usersRepository.save(new UserDAO(userCreds.getLogin(), passwordEncoder.encode(userCreds.getPassword())));
    }

    @Transactional
    public void deleteAccount(UserDetails userDetails) {
        usersRepository.deleteByLogin(userDetails.getUsername());
    }
}
