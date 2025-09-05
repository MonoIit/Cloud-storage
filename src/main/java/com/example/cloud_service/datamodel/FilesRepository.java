package com.example.cloud_service.datamodel;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends CrudRepository<FileEntity, String> {

    List<FileEntity> findAll(Pageable pageable);

    void deleteFileByFilename(String filename);

    Optional<FileEntity> findByFilename(String filename);

}
