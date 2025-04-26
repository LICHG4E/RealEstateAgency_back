package com.example.realestateagency_back.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    private String username;
    private String password;
}