package com.example.realestateagency_back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String telephone;
    private LocalDateTime createdAt;
}