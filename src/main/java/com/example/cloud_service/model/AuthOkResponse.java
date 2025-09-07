package com.example.cloud_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthOkResponse {
    @JsonProperty("auth-token")
    private String token;

    public AuthOkResponse authToken(String token) {
        this.token = token;
        return this;
    }
}
