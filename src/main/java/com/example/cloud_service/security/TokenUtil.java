package com.example.cloud_service.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenUtil implements Serializable {
    private final String HMAC_ALGO = "HmacSHA256";

    @Value("${signature.secret}")
    private String SECRET;

    public String generateToken() throws Exception {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String tokenPart = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String signature = getSignature(tokenPart);

        return tokenPart + "." + signature;

    }

    public String getSignature(String token) throws Exception {
        Mac hmac = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(), HMAC_ALGO);
        hmac.init(keySpec);
        byte[] signatureBytes = hmac.doFinal(token.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    private boolean validateSignature(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 2) return false;

        String tokenPart = parts[0];
        String signature = parts[1];

        String expected = getSignature(tokenPart);

        return expected.equals(signature);
    }

    public boolean validateToken(String login, String token) throws Exception {
        return validateSignature(token);
    }
}
