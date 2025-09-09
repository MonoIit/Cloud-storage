package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.UserDAO;
import com.example.cloud_service.datamodel.UsersRepository;
import com.example.cloud_service.model.AuthOkResponse;
import com.example.cloud_service.model.UserCreds;
import com.example.cloud_service.security.TokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final TokenUtil tokenUtil;

    public AuthOkResponse authenticate(UserCreds userCreds) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCreds.getLogin(), userCreds.getPassword()));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }

        UserDAO userDAO = usersRepository.findFirstByLogin(userCreds.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userCreds.getLogin()));

        String tokenPart;
        if (userDAO.getSignature() == null) {
            String[] token = tokenUtil.generateToken().split("\\.");
            tokenPart = token[0];
            String signature = token[1];
            userDAO.setSignature(signature);
            usersRepository.save(userDAO);
        } else {
            tokenPart = userDAO.getSignature();
        }

        return new AuthOkResponse().authToken(tokenPart);
    }

    public void logoutUser(UserDetails userDetails) {
        UserDAO userDAO = usersRepository.findFirstByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));

        userDAO.setSignature(null);
        usersRepository.save(userDAO);
    }
}
