package com.example.cloud_service.contollers;

import com.example.cloud_service.model.FileDescriptionDTO;
import com.example.cloud_service.model.FilePutRequest;
import com.example.cloud_service.model.MyUserDetails;
import com.example.cloud_service.services.FileService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileApiController {

    private final FileService service;

    public FileApiController(FileService service) {
        this.service = service;
    }

    @GetMapping("/list")
    List<FileDescriptionDTO> getAllFiles(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        return service.getLimitUserFiles(userDetails.getUserId(), limit);
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[] getFile(
            @RequestParam("filename") String filename,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) throws Exception {
        return service.getUserFileByFilename(userDetails.getUserId(), filename);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void postFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) throws Exception {
        service.uploadUserFile(userDetails.getUserId(), filename, file);
    }

    @DeleteMapping("/file")
    void deleteFile(
            @RequestParam(value = "filename", required = true) String filename,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) throws Exception {
        service.deleteUserFile(userDetails.getUserId(), filename);
    }

    @PutMapping("/file")
    void putFile(
            @RequestParam("filename") String filename,
            @RequestBody FilePutRequest filePutRequest,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        service.updateUserFileFilename(userDetails.getUserId(), filename, filePutRequest);
    }
}
