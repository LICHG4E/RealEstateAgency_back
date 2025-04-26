package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.AdminDTO;
import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AdminDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
        return convertToDTO(admin);
    }

    @Transactional
    public AdminDTO createAdmin(AdminDTO adminDTO, String rawPassword) {
        Admin admin = Admin.builder()
                .username(adminDTO.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .email(adminDTO.getEmail())
                .status(adminDTO.getStatus() != null ? adminDTO.getStatus() : "ACTIVE")
                .build();

        Admin savedAdmin = adminRepository.save(admin);
        return convertToDTO(savedAdmin);
    }

    @Transactional
    public AdminDTO updateAdmin(Long id, AdminDTO adminDTO) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));

        existingAdmin.setEmail(adminDTO.getEmail());
        existingAdmin.setStatus(adminDTO.getStatus());

        Admin updatedAdmin = adminRepository.save(existingAdmin);
        return convertToDTO(updatedAdmin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin not found with id " + id);
        }
        adminRepository.deleteById(id);
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
}