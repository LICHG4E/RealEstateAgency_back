package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.PhotoDTO;
import com.example.realestateagency_back.entity.Photo;
import com.example.realestateagency_back.entity.Property;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.PhotoRepository;
import com.example.realestateagency_back.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PropertyRepository propertyRepository;

    public List<PhotoDTO> getAllPhotos() {
        log.info("Fetching all photos");
        return photoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PhotoDTO getPhotoById(Long id) {
        log.info("Fetching photo with id: {}", id);
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Photo not found with id: {}", id);
                    return new ResourceNotFoundException("Photo not found with id " + id);
                });
        return convertToDTO(photo);
    }

    public List<PhotoDTO> getPhotosByPropertyId(Long propertyId) {
        log.info("Fetching photos for property with id: {}", propertyId);
        return photoRepository.findByPropertyIdOrderByOrderAsc(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PhotoDTO createPhoto(PhotoDTO photoDTO) {
        log.info("Creating photo for property with id: {}", photoDTO.getPropertyId());
        Property property = propertyRepository.findById(photoDTO.getPropertyId())
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", photoDTO.getPropertyId());
                    return new ResourceNotFoundException("Property not found with id " + photoDTO.getPropertyId());
                });

        Photo photo = Photo.builder()
                .url(photoDTO.getUrl())
                .order(photoDTO.getOrder())
                .property(property)
                .build();

        Photo savedPhoto = photoRepository.save(photo);
        log.debug("Photo created successfully with id: {}", savedPhoto.getId());
        return convertToDTO(savedPhoto);
    }

    @Transactional
    public PhotoDTO updatePhoto(Long id, PhotoDTO photoDTO) {
        log.info("Updating photo with id: {}", id);
        Photo existingPhoto = photoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Photo not found with id: {}", id);
                    return new ResourceNotFoundException("Photo not found with id " + id);
                });

        existingPhoto.setUrl(photoDTO.getUrl());
        existingPhoto.setOrder(photoDTO.getOrder());

        Photo updatedPhoto = photoRepository.save(existingPhoto);
        log.debug("Photo updated successfully with id: {}", updatedPhoto.getId());
        return convertToDTO(updatedPhoto);
    }

    @Transactional
    public void deletePhoto(Long id) {
        log.info("Deleting photo with id: {}", id);
        if (!photoRepository.existsById(id)) {
            log.error("Photo not found with id: {}", id);
            throw new ResourceNotFoundException("Photo not found with id " + id);
        }
        photoRepository.deleteById(id);
        log.debug("Photo deleted successfully with id: {}", id);
    }

    // Helper method to convert Photo entity to DTO
    private PhotoDTO convertToDTO(Photo photo) {
        return PhotoDTO.builder()
                .id(photo.getId())
                .url(photo.getUrl())
                .order(photo.getOrder())
                .propertyId(photo.getProperty().getId())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}