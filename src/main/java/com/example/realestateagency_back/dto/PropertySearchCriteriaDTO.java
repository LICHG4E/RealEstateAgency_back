package com.example.realestateagency_back.dto;

import com.example.realestateagency_back.entity.Property;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySearchCriteriaDTO {
    private String title;
    private String location;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minArea;
    private Double maxArea;
    private Integer rooms;
    private Property.PropertyType type;
    private Property.ListingType listingType;
}