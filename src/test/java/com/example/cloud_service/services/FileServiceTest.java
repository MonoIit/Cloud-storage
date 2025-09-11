package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.FileEntity;
import com.example.cloud_service.datamodel.FilesRepository;
import com.example.cloud_service.model.FileDescriptionDTO;
import com.example.cloud_service.model.FilePutRequest;
import com.example.cloud_service.model.ResourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.data.domain.PageRequest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private FilesRepository repo;

    @Mock
    private MinIoService minIoService;

    @InjectMocks
    private FileService fileService;

    private final String userId = "test-user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteFile_success() throws Exception {
        FileEntity entity = new FileEntity();
        entity.setUserId(userId);
        entity.setFilename("file.txt");
        entity.setFileKey("key-456");

        when(repo.findByUserIdAndFilename(userId, "file.txt")).thenReturn(Optional.of(entity));

        fileService.deleteUserFile(userId, "file.txt");

        verify(repo).deleteFileByUserIdAndFilename(userId, "file.txt");
        verify(minIoService).deleteFile("key-456");
    }

    @Test
    void getUserFileByFilename_success() throws Exception {
        FileEntity entity = new FileEntity();
        entity.setUserId(userId);
        entity.setFilename("doc.txt");
        entity.setFileKey("key-123");

        when(repo.findByUserIdAndFilename(userId, "doc.txt")).thenReturn(Optional.of(entity));
        when(minIoService.getFile("key-123")).thenReturn("content".getBytes());

        byte[] content = fileService.getUserFileByFilename(userId, "doc.txt");
        assertArrayEquals("content".getBytes(), content);
    }

    @Test
    void getUserFileByFilename_notFound_throwsException() {
        when(repo.findByUserIdAndFilename(userId, "missing.txt")).thenReturn(Optional.empty());

        assertThrows(ResourseNotFoundException.class,
                () -> fileService.getUserFileByFilename(userId, "missing.txt"));
    }

    @Test
    void uploadUserFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes()
        );

        fileService.uploadUserFile(userId, "test.txt", file);

        ArgumentCaptor<FileEntity> captor = ArgumentCaptor.forClass(FileEntity.class);
        verify(repo).save(captor.capture());
        verify(minIoService).uploadFile(anyString(), eq(file));

        FileEntity saved = captor.getValue();
        assertEquals("test.txt", saved.getFilename());
        assertEquals(userId, saved.getUserId());
    }

    @Test
    void updateUserFileFilename_success() {
        FileEntity entity = new FileEntity();
        entity.setUserId(userId);
        entity.setFilename("old.txt");

        when(repo.findByUserIdAndFilename(userId, "old.txt")).thenReturn(Optional.of(entity));

        FilePutRequest request = new FilePutRequest("new.txt");
        fileService.updateUserFileFilename(userId, "old.txt", request);

        assertEquals("new.txt", entity.getFilename());
        verify(repo).save(entity);
    }

    @Test
    void updateUserFileFilename_notFound_throwsException() {
        when(repo.findByUserIdAndFilename(userId, "missing.txt")).thenReturn(Optional.empty());

        FilePutRequest request = new FilePutRequest("new.txt");
        assertThrows(ResourseNotFoundException.class,
                () -> fileService.updateUserFileFilename(userId, "missing.txt", request));
    }

    @Test
    void getLimitUserFiles_success() {
        FileEntity f1 = new FileEntity();
        f1.setFilename("f1.txt");
        f1.setSize(16L);
        f1.setUserId(userId);

        FileEntity f2 = new FileEntity();
        f2.setFilename("f2.txt");
        f2.setSize(24L);
        f2.setUserId(userId);

        when(repo.findByUserId(userId, PageRequest.of(0, 2)))
                .thenReturn(List.of(f1, f2));

        List<FileDescriptionDTO> result = fileService.getLimitUserFiles(userId, 2);

        assertEquals(2, result.size());
        assertEquals("f1.txt", result.get(0).getFilename());
        assertEquals(16, result.get(0).getSize());
        assertEquals("f2.txt", result.get(1).getFilename());
        assertEquals(24, result.get(1).getSize());
    }
}
