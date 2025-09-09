package com.example.cloud_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.cloud_service.model.AuthOkResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Integration tests for authentication endpoints")
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String userJson = """
            {
              "login": "test-login",
              "password": "password"
            }
        """;

    @Nested
    @DisplayName("/register")
    class Register {

        @Test
        void shouldRegisterUserSuccessfully() throws Exception {
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnConflictWhenUserExists() throws Exception {
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson));

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("/login")
    class Login {

        @Test
        void shouldReturnTokenForValidCredentials() throws Exception {
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson));

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.auth-token").isString());
        }

        @Test
        void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
            String invalidJson = """
                    {
                      "email": "test-login",
                      "password": "wrong-password"
                    }
                """;
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson));

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("/logout")
    class Logout {

        @Test
        void shouldLogoutSuccessful() throws Exception {
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson));

            MvcResult result = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            AuthOkResponse response = objectMapper.readValue(responseString, AuthOkResponse.class);

            mockMvc.perform(post("/logout")
                            .header("auth-token", response.getToken()))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        void shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post("/logout"))
                    .andExpect(status().is3xxRedirection());
        }

    }

    @Nested
    @DisplayName("/account")
    class Account {

        @Test
        void shouldDeleteAccountSuccessfully() throws Exception {
            final String dummyUserJson = """
                            {
                                "login": "test-login",
                                "password": "password"
                            }
                        """;
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(dummyUserJson));

            MvcResult result = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dummyUserJson))
                    .andReturn();
            String responseString = result.getResponse().getContentAsString();
            AuthOkResponse response = objectMapper.readValue(responseString, AuthOkResponse.class);

            mockMvc.perform(delete("/account")
                            .header("auth-token", response.getToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(delete("/account"))
                    .andExpect(status().isUnauthorized());
        }
    }
}

