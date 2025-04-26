package com.example.realestateagency_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // corresponds to 'nom' in diagram

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String email;
    private String telephone; // changed from phoneNumber to match diagram

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt; // corresponds to 'date_inscription' in diagram

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}