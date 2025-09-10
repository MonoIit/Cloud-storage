package com.example.cloud_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDescriptionDTO {
    private String filename;
    private Long size;
}
