package com.example.cloud_service.datamodel;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
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

    private String mimetype;

    private Instant uploadedate;

    @Column(columnDefinition = "bytea")
    private byte[] content;

    public FileEntity(MultipartFile file) throws IOException {
        this.id = UUID.randomUUID().toString();
        this.filename = file.getOriginalFilename();
        this.mimetype = file.getContentType();
        this.uploadedate = Instant.now();
        this.content = file.getBytes();
    }
}
