package com.example.cloud_service.contollers;

import com.example.cloud_service.datamodel.FileEntity;
import com.example.cloud_service.model.FileDTO;
import com.example.cloud_service.model.FileDescriptionDTO;
import com.example.cloud_service.model.FilePutRequest;
import com.example.cloud_service.services.FileService;
import org.hibernate.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class FileApiController {

    private final FileService service;

    public FileApiController(FileService service) {
        this.service = service;
    }

    @GetMapping("/list")
    List<FileDescriptionDTO> getAllFiles(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        return service.getAllFiles(limit);
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[] getFile(
            @RequestParam("filename") String filename
    ) {
        return service.getFileByFilename(filename);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void postFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        service.postFile(filename, file);
    }

    @DeleteMapping("/file")
    void deleteFile(
            @RequestParam(value = "filename", required = true) String filename
    ) {
        service.deleteFile(filename);
    }

    @PutMapping("/file")
    void putFile(
            @RequestParam("filename") String filename,
            @RequestBody FilePutRequest filePutRequest
    ) {
        service.updateFilename(filename, filePutRequest);
    }
}
