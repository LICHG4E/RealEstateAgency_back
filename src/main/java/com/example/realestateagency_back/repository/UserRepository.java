package com.example.realestateagency_back.repository;

import com.example.realestateagency_back.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Search users by name or email containing the search term
    List<User> findByUsernameContainingOrEmailContaining(String username, String email);

    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);

    // Find users who have favorited a specific property
    @Query("SELECT u FROM User u JOIN u.favorites f WHERE f.property.id = :propertyId")
    List<User> findUsersByFavoritedPropertyId(@Param("propertyId") Long propertyId);

    // Find users who have sent messages about a specific property
    @Query("SELECT DISTINCT u FROM User u JOIN u.messages m WHERE m.property.id = :propertyId")
    List<User> findUsersByMessagePropertyId(@Param("propertyId") Long propertyId);
}