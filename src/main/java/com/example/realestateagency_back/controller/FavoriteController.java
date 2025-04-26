package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.FavoriteDTO;
import com.example.realestateagency_back.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getAllFavorites() {
        List<FavoriteDTO> favorites = favoriteService.getAllFavorites();
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoriteDTO> getFavoriteById(@PathVariable Long id) {
        FavoriteDTO favorite = favoriteService.getFavoriteById(id);
        return ResponseEntity.ok(favorite);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUserId(@PathVariable Long userId) {
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByPropertyId(@PathVariable Long propertyId) {
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByPropertyId(propertyId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/user/{userId}/property/{propertyId}")
    public ResponseEntity<FavoriteDTO> addFavorite(
            @PathVariable Long userId,
            @PathVariable Long propertyId) {
        FavoriteDTO favorite = favoriteService.addFavorite(userId, propertyId);
        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userId}/property/{propertyId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long propertyId) {
        favoriteService.removeFavorite(userId, propertyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}