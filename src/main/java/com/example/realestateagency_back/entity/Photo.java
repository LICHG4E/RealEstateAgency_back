package com.example.realestateagency_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url; // corresponds to 'url_photo' in diagram

    @Column(name = "order_num")
    private Integer order; // corresponds to 'ordre' in diagram

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}