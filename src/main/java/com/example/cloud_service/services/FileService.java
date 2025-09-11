package com.example.cloud_service.services;

import com.example.cloud_service.datamodel.FileEntity;
import com.example.cloud_service.datamodel.FilesRepository;
import com.example.cloud_service.datamodel.UserDAO;
import com.example.cloud_service.model.FileDescriptionDTO;
import com.example.cloud_service.model.FilePutRequest;
import com.example.cloud_service.model.ResourseNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class FileService {
    private final FilesRepository repo;
    private final MinIoService minIoService;


    @Transactional
    public void deleteUserFile(String userId, String filename) throws Exception {
        FileEntity file = getFileEntity(userId, filename);

        repo.deleteFileByUserIdAndFilename(userId, filename);
        minIoService.deleteFile(file.getFileKey());
    }

    public byte[] getUserFileByFilename(String userId, String filename) throws Exception {
        FileEntity file = getFileEntity(userId, filename);

        return minIoService.getFile(file.getFileKey());
    }

    public void uploadUserFile(String userId, String filename, MultipartFile file) throws Exception {
        FileEntity uploadedFile = new FileEntity(file, userId);
        uploadedFile.setFilename(filename);

        minIoService.uploadFile(uploadedFile.getFileKey(), file);

        repo.save(uploadedFile);
    }

    public void updateUserFileFilename(String userId, String filename, FilePutRequest request) {
        FileEntity file = getFileEntity(userId, filename);

        file.setFilename(request.getName());
        repo.save(file);
    }

    public List<FileDescriptionDTO> getLimitUserFiles(String userId, Integer limit) {
        List<FileEntity> found = repo.findByUserId(userId, PageRequest.of(0, limit));

        return found.stream()
                .map(e -> new FileDescriptionDTO(e.getFilename(), e.getSize()))
                .toList();
    }

    private FileEntity getFileEntity(String userId, String filename) throws ResourseNotFoundException {
        return repo.findByUserIdAndFilename(userId, filename)
                .orElseThrow(() -> new ResourseNotFoundException("file not found: " + filename));
    }
}
