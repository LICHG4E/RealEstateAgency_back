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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public List<FavoriteDTO> getAllFavorites() {
        return favoriteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FavoriteDTO> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FavoriteDTO> getFavoritesByPropertyId(Long propertyId) {
        return favoriteRepository.findByPropertyId(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FavoriteDTO getFavoriteById(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found with id " + id));
        return convertToDTO(favorite);
    }

    @Transactional
    public FavoriteDTO addFavorite(Long userId, Long propertyId) {
        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new RuntimeException("Property already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + propertyId));

        Favorite favorite = Favorite.builder()
                .user(user)
                .property(property)
                .dateAdded(LocalDateTime.now())
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return convertToDTO(savedFavorite);
    }

    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for user " + userId + " and property " + propertyId));

        favoriteRepository.delete(favorite);
    }

    @Transactional
    public void deleteFavorite(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Favorite not found with id " + id);
        }
        favoriteRepository.deleteById(id);
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