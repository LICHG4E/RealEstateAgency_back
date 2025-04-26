package com.example.realestateagency_back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime createdAt;
}