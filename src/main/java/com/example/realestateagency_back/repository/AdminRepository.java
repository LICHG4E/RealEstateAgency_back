package com.example.realestateagency_back.repository;

import com.example.realestateagency_back.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Find admins by status
    List<Admin> findByStatus(String status);

    // Search admins by username or email containing the search term
    List<Admin> findByUsernameContainingOrEmailContaining(String username, String email);

    Page<Admin> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);

    // Find admins with properties count
    @Query("SELECT a, COUNT(p) FROM Admin a LEFT JOIN a.properties p GROUP BY a")
    List<Object[]> findAdminsWithPropertyCount();

    // Find admins with at least one property
    @Query("SELECT DISTINCT a FROM Admin a JOIN a.properties p")
    List<Admin> findAdminsWithProperties();
}