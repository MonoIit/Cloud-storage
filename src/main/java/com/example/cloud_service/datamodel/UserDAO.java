package com.example.cloud_service.datamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class UserDAO {
    @Id
    private String id;

    private String login;

    private String passwordHash;

    private String signature;

    public UserDAO(String login, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.passwordHash = passwordHash;
        this.signature = null;
    }
}
