package com.example.realestateagency_back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private Long id;
    private String url;
    private Integer order;
    private Long propertyId;
    private LocalDateTime createdAt;
}