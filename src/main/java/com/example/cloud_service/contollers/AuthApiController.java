package com.example.cloud_service.contollers;

import com.example.cloud_service.model.AuthOkResponse;
import com.example.cloud_service.model.UserCreds;
import com.example.cloud_service.services.AuthService;
import com.example.cloud_service.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthApiController {
    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/login")
    public AuthOkResponse login(
            @RequestBody UserCreds authRequest
            ) throws Exception {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        authService.logoutUser(userDetails);
    }

    @PostMapping("/register")
    public void registerUser(
            @RequestBody UserCreds userCreds
    ) {
        userService.addNewUser(userCreds);
    }

    @PostMapping("/account")
    public void deleteUser(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        userService.deleteAccount(userDetails);
    }
}
