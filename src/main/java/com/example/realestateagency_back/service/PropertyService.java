package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.PhotoDTO;
import com.example.realestateagency_back.dto.PropertyDTO;
import com.example.realestateagency_back.dto.PropertySearchCriteriaDTO;
import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.entity.Property;
import com.example.realestateagency_back.entity.Photo;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.AdminRepository;
import com.example.realestateagency_back.repository.PropertyRepository;
import com.example.realestateagency_back.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final AdminRepository adminRepository;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));
        return convertToDTO(property);
    }

    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO) {
        Property property = convertToEntity(propertyDTO);

        // Set publication date and status
        property.setPublicationDate(LocalDateTime.now());
        property.setStatus(propertyDTO.getStatus() != null ? propertyDTO.getStatus() : "ACTIVE");

        // Set admin
        Admin admin = adminRepository.findById(propertyDTO.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + propertyDTO.getAdminId()));
        property.setAdmin(admin);

        Property savedProperty = propertyRepository.save(property);

        // Save photos if present
        if (propertyDTO.getPhotos() != null && !propertyDTO.getPhotos().isEmpty()) {
            for (PhotoDTO photoDTO : propertyDTO.getPhotos()) {
                // Convert PhotoDTO to Photo entity
                Photo photo = Photo.builder()
                        .url(photoDTO.getUrl())
                        .order(photoDTO.getOrder())
                        .property(savedProperty)
                        .build();
                photoRepository.save(photo);
            }
        }

        return convertToDTO(savedProperty);
    }

    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));

        // Update fields
        existingProperty.setTitle(propertyDTO.getTitle());
        existingProperty.setArea(propertyDTO.getArea());
        existingProperty.setRooms(propertyDTO.getRooms());
        existingProperty.setLocation(propertyDTO.getLocation());
        existingProperty.setPrice(propertyDTO.getPrice());
        existingProperty.setDescription(propertyDTO.getDescription());
        existingProperty.setContact(propertyDTO.getContact());
        existingProperty.setStatus(propertyDTO.getStatus());
        existingProperty.setType(propertyDTO.getType());
        existingProperty.setListingType(propertyDTO.getListingType());
        existingProperty.setUpdatedAt(LocalDateTime.now());

        Property updatedProperty = propertyRepository.save(existingProperty);

        // Update photos if needed
        if (propertyDTO.getPhotos() != null) {
            // Delete existing photos
            photoRepository.deleteByPropertyId(id);

            // Add new photos
            for (PhotoDTO photoDTO : propertyDTO.getPhotos()) {
                // Convert PhotoDTO to Photo entity
                Photo photo = Photo.builder()
                        .url(photoDTO.getUrl())
                        .order(photoDTO.getOrder())
                        .property(updatedProperty)
                        .build();
                photoRepository.save(photo);
            }
        }

        return convertToDTO(updatedProperty);
    }
    @Transactional
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Property not found with id " + id);
        }

        List<Photo> photos = photoRepository.findByPropertyIdOrderByOrderAsc(id);

        for (Photo photo : photos) {
            String fileName = photo.getUrl().substring(photo.getUrl().lastIndexOf("/") + 1);
            fileStorageService.deleteFile(fileName);
        }

        // Delete photo records from database
        photoRepository.deleteByPropertyId(id);

        // Delete the property
        propertyRepository.deleteById(id);
    }
    public List<PropertyDTO> getPropertiesByAdminId(Long adminId) {
        return propertyRepository.findByAdminId(adminId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropertyDTO> searchProperties(PropertySearchCriteriaDTO criteria) {
        return propertyRepository.findByCriteria(
                        criteria.getTitle(),
                        criteria.getLocation(),
                        criteria.getMinPrice(),
                        criteria.getMaxPrice(),
                        criteria.getMinArea(),
                        criteria.getMaxArea(),
                        criteria.getMinRooms(),
                        criteria.getMaxRooms(),
                        criteria.getType(),
                        criteria.getListingType(),
                        "ACTIVE"
                ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods to convert between entity and DTO
    private PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = PropertyDTO.builder()
                .id(property.getId())
                .title(property.getTitle())
                .area(property.getArea())
                .rooms(property.getRooms())
                .location(property.getLocation())
                .price(property.getPrice())
                .description(property.getDescription())
                .contact(property.getContact())
                .status(property.getStatus())
                .type(property.getType())
                .listingType(property.getListingType())
                .adminId(property.getAdmin() != null ? property.getAdmin().getId() : null)
                .adminUsername(property.getAdmin() != null ? property.getAdmin().getUsername() : null)
                .publicationDate(property.getPublicationDate())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();

        // Fetch photos for the property and convert to DTOs
        List<Photo> photos = photoRepository.findByPropertyIdOrderByOrderAsc(property.getId());
        List<PhotoDTO> photoDTOs = photos.stream()
                .map(photo -> PhotoDTO.builder()
                        .id(photo.getId())
                        .url(photo.getUrl())
                        .order(photo.getOrder())
                        .propertyId(property.getId())
                        .createdAt(photo.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        dto.setPhotos(photoDTOs);

        return dto;
    }

    private Property convertToEntity(PropertyDTO dto) {
        return Property.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .area(dto.getArea())
                .rooms(dto.getRooms())
                .location(dto.getLocation())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .contact(dto.getContact())
                .status(dto.getStatus())
                .type(dto.getType())
                .listingType(dto.getListingType())
                .build();
    }
}