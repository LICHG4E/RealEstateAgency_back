package com.example.realestateagency_back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private Long propertyId;
    private String propertyLocation;
    private LocalDateTime sentDate;
    private LocalDateTime createdAt;
}