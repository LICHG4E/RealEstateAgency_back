package com.example.realestateagency_back.dto;

import com.example.realestateagency_back.entity.Photo;
import com.example.realestateagency_back.entity.Property;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private Long id;
    private Double area;
    private Integer rooms;
    private String location;
    private BigDecimal price;
    private String description;
    private String contactInfo;
    private String status;
    private Property.PropertyType type;
    private Property.ListingType listingType;
    private Long adminId;
    private String adminUsername;
    private LocalDateTime publicationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Photo> photos; // For simplified photo representation
}