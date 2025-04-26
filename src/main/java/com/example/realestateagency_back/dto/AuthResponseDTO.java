package com.example.realestateagency_back.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String userType; // "ADMIN" or "USER"
}