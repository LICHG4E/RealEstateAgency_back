package com.example.realestateagency_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // corresponds to 'nom' in diagram

    @Column(nullable = false)
    private String password; // corresponds to 'mot_de_passe' in diagram

    private String email;

    private String status; // 'statut' in diagram

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt; // corresponds to 'date_creation' in diagram

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}