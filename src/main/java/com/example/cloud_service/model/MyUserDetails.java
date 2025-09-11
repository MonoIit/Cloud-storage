package com.example.cloud_service.model;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MyUserDetails extends User {
    private final String userId;

    public MyUserDetails(String username, String password, String userId) {
        super(username, password, List.of());
        this.userId = userId;
    }
}
