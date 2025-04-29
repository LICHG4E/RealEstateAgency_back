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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final AdminRepository adminRepository;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public List<PropertyDTO> getAllProperties() {
        log.info("Fetching all properties");
        List<PropertyDTO> properties = propertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} properties", properties.size());
        return properties;
    }

    public PropertyDTO getPropertyById(Long id) {
        log.info("Fetching property with id: {}", id);
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", id);
                    return new ResourceNotFoundException("Property not found with id " + id);
                });
        log.debug("Property found with id: {}", id);
        return convertToDTO(property);
    }

    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO) {
        log.info("Creating new property with title: {}", propertyDTO.getTitle());
        Property property = convertToEntity(propertyDTO);

        // Set publication date and status
        property.setPublicationDate(LocalDateTime.now());
        property.setStatus(propertyDTO.getStatus() != null ? propertyDTO.getStatus() : "ACTIVE");

        // Set admin
        log.debug("Fetching admin with id: {}", propertyDTO.getAdminId());
        Admin admin = adminRepository.findById(propertyDTO.getAdminId())
                .orElseThrow(() -> {
                    log.error("Admin not found with id: {}", propertyDTO.getAdminId());
                    return new ResourceNotFoundException("Admin not found with id " + propertyDTO.getAdminId());
                });
        property.setAdmin(admin);

        Property savedProperty = propertyRepository.save(property);
        log.debug("Property created successfully with id: {}", savedProperty.getId());

        // Save photos if present
        if (propertyDTO.getPhotos() != null && !propertyDTO.getPhotos().isEmpty()) {
            log.debug("Adding {} photos to property", propertyDTO.getPhotos().size());
            for (PhotoDTO photoDTO : propertyDTO.getPhotos()) {
                // Convert PhotoDTO to Photo entity
                Photo photo = Photo.builder()
                        .url(photoDTO.getUrl())
                        .order(photoDTO.getOrder())
                        .property(savedProperty)
                        .build();
                photoRepository.save(photo);
            }
            log.debug("Photos added successfully to property: {}", savedProperty.getId());
        }

        return convertToDTO(savedProperty);
    }

    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) {
        log.info("Updating property with id: {}", id);
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", id);
                    return new ResourceNotFoundException("Property not found with id " + id);
                });

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
        log.debug("Property basic information updated successfully with id: {}", updatedProperty.getId());

        if (propertyDTO.getPhotos() != null) {
            log.debug("Updating photos for property with id: {}", id);
            photoRepository.deleteByPropertyId(id);
            log.debug("Old photos deleted for property with id: {}", id);

            for (PhotoDTO photoDTO : propertyDTO.getPhotos()) {
                // Convert PhotoDTO to Photo entity
                Photo photo = Photo.builder()
                        .url(photoDTO.getUrl())
                        .order(photoDTO.getOrder())
                        .property(updatedProperty)
                        .build();
                photoRepository.save(photo);
            }
            log.debug("New photos added for property with id: {}", id);
        }

        log.info("Property updated successfully with id: {}", updatedProperty.getId());
        return convertToDTO(updatedProperty);
    }

    @Transactional
    public void deleteProperty(Long id) {
        log.info("Deleting property with id: {}", id);
        if (!propertyRepository.existsById(id)) {
            log.error("Property not found with id: {}", id);
            throw new ResourceNotFoundException("Property not found with id " + id);
        }

        List<Photo> photos = photoRepository.findByPropertyIdOrderByOrderAsc(id);
        log.debug("Found {} photos to delete for property with id: {}", photos.size(), id);

        for (Photo photo : photos) {
            String fileName = photo.getUrl().substring(photo.getUrl().lastIndexOf("/") + 1);
            log.debug("Deleting photo file: {}", fileName);
            fileStorageService.deleteFile(fileName);
        }

        // Delete photo records from database
        log.debug("Deleting photo records from database for property with id: {}", id);
        photoRepository.deleteByPropertyId(id);

        // Delete the property
        log.debug("Deleting property record with id: {}", id);
        propertyRepository.deleteById(id);
        log.info("Property deleted successfully with id: {}", id);
    }

    public List<PropertyDTO> getPropertiesByAdminId(Long adminId) {
        log.info("Fetching properties for admin with id: {}", adminId);
        List<PropertyDTO> properties = propertyRepository.findByAdminId(adminId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} properties for admin with id: {}", properties.size(), adminId);
        return properties;
    }

    public List<PropertyDTO> searchProperties(PropertySearchCriteriaDTO criteria) {
        log.info("Searching properties with criteria: location={}, price range={}-{}, area range={}-{}, rooms range={}-{}",
                criteria.getLocation(), criteria.getMinPrice(), criteria.getMaxPrice(),
                criteria.getMinArea(), criteria.getMaxArea(), criteria.getMinRooms(), criteria.getMaxRooms());

        List<PropertyDTO> results = propertyRepository.findByCriteria(
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
                        "ACTIVE" // Default to active properties for search
                ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.debug("Search returned {} results", results.size());
        return results;
    }

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