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
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private FilesRepository repo;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteFile_success() {
        fileService.deleteFile("test.txt");
        verify(repo).deleteFileByFilename("test.txt");
    }

    @Test
    void getFileByFilename_success() {
        FileEntity entity = new FileEntity();
        entity.setFilename("doc.txt");
        entity.setContent("hello".getBytes());

        when(repo.findByFilename("doc.txt")).thenReturn(Optional.of(entity));

        byte[] content = fileService.getFileByFilename("doc.txt");

        assertArrayEquals("hello".getBytes(), content);
    }

    @Test
    void getFileByFilename_notFound_throwsException() {
        when(repo.findByFilename("missing.txt")).thenReturn(Optional.empty());

        assertThrows(ResourseNotFoundException.class,
                () -> fileService.getFileByFilename("missing.txt"));
    }

    @Test
    void postFile_success() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenReturn("data".getBytes());

        fileService.postFile("uploaded.txt", multipartFile);

        ArgumentCaptor<FileEntity> captor = ArgumentCaptor.forClass(FileEntity.class);
        verify(repo).save(captor.capture());

        FileEntity saved = captor.getValue();
        assertEquals("uploaded.txt", saved.getFilename());
        assertArrayEquals("data".getBytes(), saved.getContent());
    }

    @Test
    void updateFilename_success() {
        FileEntity entity = new FileEntity();
        entity.setFilename("old.txt");
        entity.setContent("123".getBytes());

        when(repo.findByFilename("old.txt")).thenReturn(Optional.of(entity));

        FilePutRequest request = new FilePutRequest();
        request.setName("new.txt");

        fileService.updateFilename("old.txt", request);

        assertEquals("new.txt", entity.getFilename());
        verify(repo).save(entity);
    }

    @Test
    void updateFilename_notFound_throwsException() {
        when(repo.findByFilename("missing.txt")).thenReturn(Optional.empty());

        FilePutRequest request = new FilePutRequest();
        request.setName("new.txt");

        assertThrows(ResourseNotFoundException.class,
                () -> fileService.updateFilename("missing.txt", request));
    }

    @Test
    void getAllFiles_success() {
        FileEntity f1 = new FileEntity();
        f1.setFilename("f1.txt");
        f1.setContent(new byte[16]);

        FileEntity f2 = new FileEntity();
        f2.setFilename("f2.txt");
        f2.setContent(new byte[24]);

        when(repo.findAll(PageRequest.of(0, 2)))
                .thenReturn(List.of(f1, f2));

        List<FileDescriptionDTO> result = fileService.getAllFiles(2);

        assertEquals(2, result.size());
        assertEquals("f1.txt", result.get(0).getFilename());
        assertEquals(16, result.get(0).getSize());
        assertEquals("f2.txt", result.get(1).getFilename());
        assertEquals(24, result.get(1).getSize());
    }
}
