package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.PropertyDTO;
import com.example.realestateagency_back.dto.PropertySearchCriteriaDTO;
import com.example.realestateagency_back.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PropertyController {

    private final PropertyService propertyService;

    // Publicly accessible endpoints
    @GetMapping("/public/all")
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        List<PropertyDTO> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        PropertyDTO property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @PostMapping("/public/search")
    public ResponseEntity<List<PropertyDTO>> searchProperties(@RequestBody PropertySearchCriteriaDTO criteria) {
        List<PropertyDTO> properties = propertyService.searchProperties(criteria);
        return ResponseEntity.ok(properties);
    }

    // Admin only endpoints
    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyDTO propertyDTO) {
        PropertyDTO createdProperty = propertyService.createProperty(propertyDTO);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyDTO propertyDTO) {
        PropertyDTO updatedProperty = propertyService.updateProperty(id, propertyDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<PropertyDTO>> getPropertiesByAdminId(@PathVariable Long adminId) {
        List<PropertyDTO> properties = propertyService.getPropertiesByAdminId(adminId);
        return ResponseEntity.ok(properties);
    }
}