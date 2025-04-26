package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.AuthRequestDTO;
import com.example.realestateagency_back.dto.AuthResponseDTO;
import com.example.realestateagency_back.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/admin")
    public ResponseEntity<AuthResponseDTO> authenticateAdmin(@RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO response = authService.authenticateAdmin(authRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/user")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO response = authService.authenticateUser(authRequestDTO);
        return ResponseEntity.ok(response);
    }
}