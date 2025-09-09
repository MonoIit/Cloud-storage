package com.example.cloud_service.datamodel;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<UserDAO, String> {

    Optional<UserDAO> findFirstByLogin(String login);

    Optional<UserDAO> findFirstBySignature(String signature);

    void deleteByLogin(String login);
}
