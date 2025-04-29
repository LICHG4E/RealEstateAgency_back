package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.AdminDTO;
import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminDTO> getAllAdmins() {
        log.info("Fetching all admin accounts");
        return adminRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AdminDTO getAdminById(Long id) {
        log.info("Fetching admin with id: {}", id);
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Admin not found with id: {}", id);
                    return new ResourceNotFoundException("Admin not found with id " + id);
                });
        return convertToDTO(admin);
    }

    @Transactional
    public AdminDTO createAdmin(AdminDTO adminDTO, String rawPassword) {
        log.info("Creating new admin with username: {}", adminDTO.getUsername());
        Admin admin = Admin.builder()
                .username(adminDTO.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .email(adminDTO.getEmail())
                .status(adminDTO.getStatus() != null ? adminDTO.getStatus() : "ACTIVE")
                .build();

        Admin savedAdmin = adminRepository.save(admin);
        log.debug("Admin created successfully with id: {}", savedAdmin.getId());
        return convertToDTO(savedAdmin);
    }

    @Transactional
    public AdminDTO updateAdmin(Long id, AdminDTO adminDTO) {
        log.info("Updating admin with id: {}", id);
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Admin not found with id: {}", id);
                    return new ResourceNotFoundException("Admin not found with id " + id);
                });

        existingAdmin.setEmail(adminDTO.getEmail());
        existingAdmin.setStatus(adminDTO.getStatus());

        Admin updatedAdmin = adminRepository.save(existingAdmin);
        log.debug("Admin updated successfully with id: {}", updatedAdmin.getId());
        return convertToDTO(updatedAdmin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        log.info("Deleting admin with id: {}", id);
        if (!adminRepository.existsById(id)) {
            log.error("Admin not found with id: {}", id);
            throw new ResourceNotFoundException("Admin not found with id " + id);
        }
        adminRepository.deleteById(id);
        log.debug("Admin deleted successfully with id: {}", id);
    }

    // Helper method to convert admin entity to DTO
    private AdminDTO convertToDTO(Admin admin) {
        return AdminDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .status(admin.getStatus())
                .createdAt(admin.getCreatedAt())
                .build();
    }

    public int countAdmins() {
        log.info("Counting total number of admins");
        int count = (int) adminRepository.count();
        log.debug("Total admin count: {}", count);
        return count;
    }
}