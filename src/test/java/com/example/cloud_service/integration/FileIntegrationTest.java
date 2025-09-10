package com.example.cloud_service.integration;

import com.example.cloud_service.model.AuthOkResponse;
import com.example.cloud_service.model.FilePutRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for fileAPI endpoints")
@ActiveProfiles("test")
class FileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static String token;

    private final MockMultipartFile testFile = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE,
            "test content".getBytes(StandardCharsets.UTF_8)
    );

    @BeforeAll
    static void registerUser(
            @Autowired MockMvc mockMvc,
            @Autowired ObjectMapper objectMapper
    ) throws Exception {
        String userJson = """
                {
                    "login": "test-login",
                    "password": "password"
                }
            """;
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        AuthOkResponse response = objectMapper.readValue(responseString, AuthOkResponse.class);
        token = response.getToken();
    }


    @BeforeEach
    void cleanUp() {
        jdbcTemplate.execute("DELETE FROM file_entity");
    }

    private void addFile(String filename, MockMultipartFile file) throws Exception {
        mockMvc.perform(multipart("/file")
                        .file(file)
                        .param("filename", filename)
                .header("auth-token", token));
    }

    @Nested
    @DisplayName("/list")
    class List {

        @Test
        void shouldReturnListOfFiles() throws Exception {
            String filename = "file2.txt";
            String content = "test content";
            MockMultipartFile file = new MockMultipartFile(
                    "file", filename, MediaType.TEXT_PLAIN_VALUE,
                    content.getBytes(StandardCharsets.UTF_8)
            );


            addFile("file1.txt", testFile);
            addFile("file2.txt", file);

            mockMvc.perform(get("/list")
                            .param("limit", "5")
                            .header("auth-token", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].filename").value("file1.txt"))
                    .andExpect(jsonPath("$[1].size").value(content.length()));
        }

    }

    @Nested
    @DisplayName("/file")
    class File {

        @Test
        void shouldUploadFile() throws Exception {
            mockMvc.perform(multipart("/file")
                            .file(testFile)
                            .param("filename", "test.txt")
                            .header("auth-token", token))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnFileContent() throws Exception {
            String filename = "testFile";
            addFile(filename, testFile);

            mockMvc.perform(get("/file")
                            .param("filename", filename)
                            .header("auth-token", token))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(testFile.getBytes()));
        }

        @Test
        void shouldDeleteFile() throws Exception {
            String filename = "testFile";
            addFile(filename, testFile);

            mockMvc.perform(get("/file")
                            .param("filename", filename)
                            .header("auth-token", token))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/file")
                            .param("filename", filename)
                            .header("auth-token", token))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/file")
                            .param("filename", filename)
                            .header("auth-token", token))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldUpdateFile() throws Exception {
            FilePutRequest request = new FilePutRequest("newTestFile");
            String oldFilename = "oldTestFile";
            addFile(oldFilename, testFile);

            mockMvc.perform(get("/file")
                            .param("filename", oldFilename)
                            .header("auth-token", token))
                    .andExpect(status().isOk());

            mockMvc.perform(put("/file")
                            .param("filename", oldFilename)
                            .header("auth-token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/file")
                            .param("filename", oldFilename)
                            .header("auth-token", token))
                    .andExpect(status().isNotFound());

            mockMvc.perform(get("/file")
                            .param("filename", "newTestFile")
                            .header("auth-token", token))
                    .andExpect(status().isOk());
        }

    }
}

