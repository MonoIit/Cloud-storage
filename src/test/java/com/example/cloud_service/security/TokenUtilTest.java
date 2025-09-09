package com.example.cloud_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilTest {

    private TokenUtil tokenUtil;

    @BeforeEach
    void setUp() {
        tokenUtil = new TokenUtil("mysecret123");
    }

    @Test
    void generateToken_createsTokenWithTwoParts() throws Exception {
        String token = tokenUtil.generateToken();
        String[] parts = token.split("\\.");
        assertEquals(2, parts.length);
        assertFalse(parts[0].isEmpty());
        assertFalse(parts[1].isEmpty());
    }

    @Test
    void getSignature_sameInput_returnsSameSignature() throws Exception {
        String tokenPart = "abc123";
        String sig1 = tokenUtil.getSignature(tokenPart);
        String sig2 = tokenUtil.getSignature(tokenPart);
        assertEquals(sig1, sig2);
    }

    @Test
    void getSignature_differentInput_returnsDifferentSignature() throws Exception {
        String sig1 = tokenUtil.getSignature("abc1");
        String sig2 = tokenUtil.getSignature("abc2");
        assertNotEquals(sig1, sig2);
    }

    @Test
    void validateToken_validToken_returnsTrue() throws Exception {
        String token = tokenUtil.generateToken();
        assertTrue(tokenUtil.validateToken("login", token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() throws Exception {
        String token = tokenUtil.generateToken();
        String tampered = token + "a"; // портим токен
        assertFalse(tokenUtil.validateToken("login", tampered));
    }
}
