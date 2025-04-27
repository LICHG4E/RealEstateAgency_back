package com.example.realestateagency_back.repository;

import com.example.realestateagency_back.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find by admin
    List<Property> findByAdminId(Long adminId);
    Page<Property> findByAdminId(Long adminId, Pageable pageable);

    // Find by property characteristics
    List<Property> findByType(Property.PropertyType type);
    List<Property> findByListingType(Property.ListingType listingType);
    List<Property> findByStatus(String status);
    List<Property> findByRooms(Integer rooms);

    // Location-based search (with LIKE for partial matches)
    List<Property> findByLocationContaining(String location);

    // Price-range searches
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Property> findByPriceLessThanEqual(BigDecimal maxPrice);
    List<Property> findByPriceGreaterThanEqual(BigDecimal minPrice);

    // Area-range searches
    List<Property> findByAreaBetween(Double minArea, Double maxArea);
    List<Property> findByAreaLessThanEqual(Double maxArea);
    List<Property> findByAreaGreaterThanEqual(Double minArea);

    // Combined search criteria
    @Query("SELECT p FROM Property p WHERE " +
            "(:title IS NULL OR p.title LIKE %:title%) AND " +
            "(:location IS NULL OR p.location LIKE %:location%) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minArea IS NULL OR p.area >= :minArea) AND " +
            "(:maxArea IS NULL OR p.area <= :maxArea) AND " +
            "(:rooms IS NULL OR p.rooms = :rooms) AND " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:listingType IS NULL OR p.listingType = :listingType) AND " +
            "(:status IS NULL OR p.status = :status)")
    List<Property> findByCriteria(
            @Param("title") String title,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            @Param("rooms") Integer rooms,
            @Param("type") Property.PropertyType type,
            @Param("listingType") Property.ListingType listingType,
            @Param("status") String status
    );

    // Same as above but with pagination
    @Query("SELECT p FROM Property p WHERE " +
            "(:title IS NULL OR p.title LIKE %:title%) AND " +
            "(:location IS NULL OR p.location LIKE %:location%) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minArea IS NULL OR p.area >= :minArea) AND " +
            "(:maxArea IS NULL OR p.area <= :maxArea) AND " +
            "(:rooms IS NULL OR p.rooms = :rooms) AND " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:listingType IS NULL OR p.listingType = :listingType) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Property> findByCriteria(
            @Param("title") String title,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            @Param("rooms") Integer rooms,
            @Param("type") Property.PropertyType type,
            @Param("listingType") Property.ListingType listingType,
            @Param("status") String status,
            Pageable pageable
    );

    // Count properties by admin
    long countByAdminId(Long adminId);
}