package com.example.realestateagency_back.repository;

import com.example.realestateagency_back.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);

    List<Favorite> findByPropertyId(Long propertyId);

    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);

    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);

    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}