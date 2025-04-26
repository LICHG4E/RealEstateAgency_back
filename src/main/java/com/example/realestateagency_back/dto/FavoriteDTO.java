package com.example.realestateagency_back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long propertyId;
    private String propertyLocation;
    private LocalDateTime dateAdded;
    private LocalDateTime createdAt;
}