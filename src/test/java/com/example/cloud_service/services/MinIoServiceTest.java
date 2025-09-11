package com.example.cloud_service.services;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class MinIoServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinIoService minIoService;

    private final String BUCKET = "my-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFile_bucketDoesNotExist_createsBucketAndUploads() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes()
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minIoService.uploadFile("key1", file);

        verify(minioClient).makeBucket(argThat(args -> BUCKET.equals(args.bucket())));
        verify(minioClient).putObject(argThat(args -> BUCKET.equals(args.bucket()) &&
                "key1".equals(args.object())));
    }

    @Test
    void uploadFile_bucketExists_uploadsOnly() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes()
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minIoService.uploadFile("key2", file);

        verify(minioClient, never()).makeBucket(any());
        verify(minioClient).putObject(argThat(args -> "key2".equals(args.object())));
    }

    @Test
    void getFile_returnsContent() throws Exception {
        byte[] content = "hello".getBytes();

        GetObjectResponse response = mock(GetObjectResponse.class);
        when(response.readAllBytes()).thenReturn(content);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        byte[] result = minIoService.getFile("key3");

        assertArrayEquals(content, result);

        verify(response).readAllBytes();
    }

    @Test
    void deleteFile_callsRemoveObject() throws Exception {
        minIoService.deleteFile("key4");

        verify(minioClient).removeObject(argThat(args -> BUCKET.equals(args.bucket()) &&
                "key4".equals(args.object())));
    }
}
