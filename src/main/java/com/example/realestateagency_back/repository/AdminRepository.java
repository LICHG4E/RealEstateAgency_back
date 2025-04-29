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


    Optional<Admin> findByEmail(String email);


    boolean existsByEmail(String email);

}