package com.example.cloud_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreds {

    @JsonProperty("login")
    private final String login;

    @JsonProperty("password")
    private final String password;

}
