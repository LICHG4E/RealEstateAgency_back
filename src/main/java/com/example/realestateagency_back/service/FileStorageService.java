package com.example.realestateagency_back.service;

import com.example.realestateagency_back.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            log.info("Creating file storage directory at: {}", this.fileStorageLocation);
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Could not create the directory for file storage", ex);
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        log.info("Storing file: {}", file.getOriginalFilename());
        // Generate unique file name
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                log.error("Invalid file name detected: {}", fileName);
                throw new FileStorageException("Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            log.debug("Copying file to: {}", targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", fileName);
            return fileName;
        } catch (IOException ex) {
            log.error("Could not store file: {}", fileName, ex);
            throw new FileStorageException("Could not store file " + fileName, ex);
        }
    }

    public Path getFilePath(String fileName) {
        log.debug("Getting file path for: {}", fileName);
        return this.fileStorageLocation.resolve(fileName);
    }

    public void deleteFile(String fileName) {
        log.info("Deleting file: {}", fileName);
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName);
            log.debug("Deleting file at path: {}", filePath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("File deleted successfully: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }
        } catch (IOException ex) {
            log.error("Could not delete file: {}", fileName, ex);
            throw new FileStorageException("Could not delete file " + fileName, ex);
        }
    }
}