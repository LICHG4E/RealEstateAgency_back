package com.example.realestateagency_back.repository;

import com.example.realestateagency_back.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByPropertyId(Long propertyId);

    List<Photo> findByPropertyIdOrderByOrderAsc(Long propertyId);

    void deleteByPropertyId(Long propertyId);
}