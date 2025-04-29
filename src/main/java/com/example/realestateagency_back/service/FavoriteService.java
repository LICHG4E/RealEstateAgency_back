package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.FavoriteDTO;
import com.example.realestateagency_back.entity.Favorite;
import com.example.realestateagency_back.entity.Property;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.FavoriteRepository;
import com.example.realestateagency_back.repository.PropertyRepository;
import com.example.realestateagency_back.repository.UserRepository;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public List<FavoriteDTO> getAllFavorites() {
        log.info("Fetching all favorites");
        return favoriteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FavoriteDTO> getFavoritesByUserId(Long userId) {
        log.info("Fetching favorites for user with id: {}", userId);
        return favoriteRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FavoriteDTO> getFavoritesByPropertyId(Long propertyId) {
        log.info("Fetching favorites for property with id: {}", propertyId);
        return favoriteRepository.findByPropertyId(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FavoriteDTO getFavoriteById(Long id) {
        log.info("Fetching favorite with id: {}", id);
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Favorite not found with id: {}", id);
                    return new ResourceNotFoundException("Favorite not found with id " + id);
                });
        return convertToDTO(favorite);
    }

    @Transactional
    public FavoriteDTO addFavorite(Long userId, Long propertyId) {
        log.info("Adding favorite for user: {} and property: {}", userId, propertyId);
        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            log.warn("Property already in favorites for user: {} and property: {}", userId, propertyId);
            throw new RuntimeException("Property already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id " + userId);
                });

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", propertyId);
                    return new ResourceNotFoundException("Property not found with id " + propertyId);
                });

        Favorite favorite = Favorite.builder()
                .user(user)
                .property(property)
                .dateAdded(LocalDateTime.now())
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.debug("Favorite added successfully with id: {}", savedFavorite.getId());
        return convertToDTO(savedFavorite);
    }

    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        log.info("Removing favorite for user: {} and property: {}", userId, propertyId);
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> {
                    log.error("Favorite not found for user: {} and property: {}", userId, propertyId);
                    return new ResourceNotFoundException("Favorite not found for user " + userId + " and property " + propertyId);
                });

        favoriteRepository.delete(favorite);
        log.debug("Favorite removed successfully for user: {} and property: {}", userId, propertyId);
    }

    @Transactional
    public void deleteFavorite(Long id) {
        log.info("Deleting favorite with id: {}", id);
        if (!favoriteRepository.existsById(id)) {
            log.error("Favorite not found with id: {}", id);
            throw new ResourceNotFoundException("Favorite not found with id " + id);
        }
        favoriteRepository.deleteById(id);
        log.debug("Favorite deleted successfully with id: {}", id);
    }

    // Helper method to convert Favorite entity to DTO
    private FavoriteDTO convertToDTO(Favorite favorite) {
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .username(favorite.getUser().getUsername())
                .propertyId(favorite.getProperty().getId())
                .propertyLocation(favorite.getProperty().getLocation())
                .dateAdded(favorite.getDateAdded())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}