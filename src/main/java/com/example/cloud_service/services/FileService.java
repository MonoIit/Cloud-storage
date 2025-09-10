package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.FileEntity;
import com.example.cloud_service.datamodel.FilesRepository;
import com.example.cloud_service.model.FileDescriptionDTO;
import com.example.cloud_service.model.FilePutRequest;
import com.example.cloud_service.model.ResourseNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final FilesRepository repo;
    private final ModelMapper mapper;

    public FileService(FilesRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public void deleteFile(String filename) {
        repo.deleteFileByFilename(filename);
    }

    public byte[] getFileByFilename(String filename) {
        FileEntity file = repo.findByFilename(filename)
                .orElseThrow(() -> new ResourseNotFoundException("file " + filename));

        return file.getContent();
    }

    public void postFile(String filename, MultipartFile file) throws IOException {
        FileEntity uploadedFile = new FileEntity(file);
        uploadedFile.setFilename(filename);
        repo.save(uploadedFile);
    }

    public void updateFilename(String filename, FilePutRequest request) {
        FileEntity file = repo.findByFilename(filename)
                .orElseThrow(() -> new ResourseNotFoundException("file " + filename));

        file.setFilename(request.getName());
        repo.save(file);
    }

    public List<FileDescriptionDTO> getAllFiles(Integer limit) {
        List<FileEntity> found = repo.findAll(PageRequest.of(0, limit));

        return found.stream()
                .map(e -> new FileDescriptionDTO(e.getFilename(), (long) e.getContent().length))
                .toList();
    }
}
