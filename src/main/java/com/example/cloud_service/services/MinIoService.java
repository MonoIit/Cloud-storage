package com.example.cloud_service.services;

import io.minio.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@AllArgsConstructor
public class MinIoService {
    private final MinioClient minioClient;

    private final String BUCKET = "my-bucket";

    public void uploadFile(String fileKey, MultipartFile file) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(BUCKET)
                        .object(fileKey)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }

    public String getBucket() {
        return BUCKET;
    }

    public byte[] getFile(String fileKey) throws Exception {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(BUCKET).object(fileKey).build())) {
            return stream.readAllBytes();
        }
    }

    public void deleteFile(String fileKey) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(BUCKET).object(fileKey).build());
    }
}
