package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.AdminDTO;
import com.example.realestateagency_back.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        List<AdminDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        AdminDTO admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public ResponseEntity<AdminDTO> createAdmin(
            @Valid @RequestBody AdminDTO adminDTO,
            @RequestParam String password) {
        AdminDTO createdAdmin = adminService.createAdmin(adminDTO, password);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminDTO> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminDTO adminDTO) {
        AdminDTO updatedAdmin = adminService.updateAdmin(id, adminDTO);
        return ResponseEntity.ok(updatedAdmin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}