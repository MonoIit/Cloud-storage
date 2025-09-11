package com.example.cloud_service.datamodel;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class FileEntity {

    @Id
    private String id;

    private String filename;

    private String fileKey;

    private String contentType;

    private Long size;

    private String userId;

    private Instant uploadedDate;

    public FileEntity(MultipartFile file, String userId) throws IOException {
        this.id = UUID.randomUUID().toString();
        this.filename = file.getOriginalFilename();
        this.fileKey = UUID.randomUUID().toString();
        this.contentType = file.getContentType();
        this.size = file.getSize();
        this.userId = userId;
        this.uploadedDate = Instant.now();
    }
}
