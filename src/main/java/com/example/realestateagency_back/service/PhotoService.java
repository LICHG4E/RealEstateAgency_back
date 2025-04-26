package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.PhotoDTO;
import com.example.realestateagency_back.entity.Photo;
import com.example.realestateagency_back.entity.Property;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.PhotoRepository;
import com.example.realestateagency_back.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PropertyRepository propertyRepository;

    public List<PhotoDTO> getAllPhotos() {
        return photoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PhotoDTO getPhotoById(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id " + id));
        return convertToDTO(photo);
    }

    public List<PhotoDTO> getPhotosByPropertyId(Long propertyId) {
        return photoRepository.findByPropertyIdOrderByOrderAsc(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PhotoDTO createPhoto(PhotoDTO photoDTO) {
        Property property = propertyRepository.findById(photoDTO.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + photoDTO.getPropertyId()));

        Photo photo = Photo.builder()
                .url(photoDTO.getUrl())
                .order(photoDTO.getOrder())
                .property(property)
                .build();

        Photo savedPhoto = photoRepository.save(photo);
        return convertToDTO(savedPhoto);
    }

    @Transactional
    public PhotoDTO updatePhoto(Long id, PhotoDTO photoDTO) {
        Photo existingPhoto = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id " + id));

        existingPhoto.setUrl(photoDTO.getUrl());
        existingPhoto.setOrder(photoDTO.getOrder());

        Photo updatedPhoto = photoRepository.save(existingPhoto);
        return convertToDTO(updatedPhoto);
    }

    @Transactional
    public void deletePhoto(Long id) {
        if (!photoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Photo not found with id " + id);
        }
        photoRepository.deleteById(id);
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