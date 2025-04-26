package com.example.realestateagency_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double area; // corresponds to 'superficie' in diagram
    private Integer rooms; // corresponds to 'nombre_pieces' in diagram
    private String location; // corresponds to 'localisation' in diagram
    private BigDecimal price; // corresponds to 'prix' in diagram

    @Column(columnDefinition = "TEXT")
    private String description;

    private String contactInfo; // corresponds to 'contact' in diagram

    private String status; // 'statut' in diagram

    @Enumerated(EnumType.STRING)
    private PropertyType type; // corresponds to 'type_bien' in diagram

    @Enumerated(EnumType.STRING)
    private ListingType listingType; // additional field not in diagram

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt; // for auditing

    @Column(name = "publication_date")
    private LocalDateTime publicationDate; // corresponds to 'date_publication' in diagram

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        publicationDate = createdAt;
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum PropertyType {
        HOUSE,
        LAND
    }

    public enum ListingType {
        SALE,
        RENT
    }
}