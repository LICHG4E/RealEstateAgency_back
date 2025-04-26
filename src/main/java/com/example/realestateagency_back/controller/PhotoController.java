package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.PhotoDTO;
import com.example.realestateagency_back.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoController {

    private final PhotoService photoService;

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

    @PostMapping
    public ResponseEntity<PhotoDTO> createPhoto(@Valid @RequestBody PhotoDTO photoDTO) {
        PhotoDTO createdPhoto = photoService.createPhoto(photoDTO);
        return new ResponseEntity<>(createdPhoto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoDTO> updatePhoto(
            @PathVariable Long id,
            @Valid @RequestBody PhotoDTO photoDTO) {
        PhotoDTO updatedPhoto = photoService.updatePhoto(id, photoDTO);
        return ResponseEntity.ok(updatedPhoto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}