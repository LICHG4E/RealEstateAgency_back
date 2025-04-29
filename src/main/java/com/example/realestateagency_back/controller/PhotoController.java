package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.PhotoDTO;
import com.example.realestateagency_back.service.FileStorageService;
import com.example.realestateagency_back.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoController {

    private final PhotoService photoService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<PhotoDTO>> getAllPhotos() {
        List<PhotoDTO> photos = photoService.getAllPhotos();
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDTO> getPhotoById(@PathVariable Long id) {
        PhotoDTO photo = photoService.getPhotoById(id);
        return ResponseEntity.ok(photo);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<PhotoDTO>> getPhotosByPropertyId(@PathVariable Long propertyId) {
        List<PhotoDTO> photos = photoService.getPhotosByPropertyId(propertyId);
        return ResponseEntity.ok(photos);
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotoDTO> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("propertyId") Long propertyId,
            @RequestParam("order") Integer order) {

        // Store the file
        String fileName = fileStorageService.storeFile(file);

        // Create download URL
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/photos/download/")
                .path(fileName)
                .toUriString();

        // Create photo record in database
        PhotoDTO photoDTO = PhotoDTO.builder()
                .url(fileDownloadUri)
                .order(order)
                .propertyId(propertyId)
                .build();

        PhotoDTO createdPhoto = photoService.createPhoto(photoDTO);
        return new ResponseEntity<>(createdPhoto, HttpStatus.CREATED);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Adjust this as needed based on file type
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        PhotoDTO photo = photoService.getPhotoById(id);

        String fileName = photo.getUrl().substring(photo.getUrl().lastIndexOf("/") + 1);

        fileStorageService.deleteFile(fileName);

        photoService.deletePhoto(id);

        return ResponseEntity.noContent().build();
    }
}