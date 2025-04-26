package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.AuthRequestDTO;
import com.example.realestateagency_back.dto.AuthResponseDTO;
import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.repository.AdminRepository;
import com.example.realestateagency_back.repository.UserRepository;
import com.example.realestateagency_back.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AuthResponseDTO authenticateAdmin(AuthRequestDTO authRequestDTO) {
        try {
            System.out.printf("Authenticating admin with email: %s%n", authRequestDTO.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
            );

            Optional<Admin> admin = adminRepository.findByEmail(authRequestDTO.getEmail());
            if (admin.isEmpty()) {
                throw new RuntimeException("Admin not found");
            }

            String jwt = jwtUtils.generateToken(authRequestDTO.getEmail(), "ADMIN");

            return AuthResponseDTO.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(admin.get().getId())
                    .username(admin.get().getUsername())
                    .userType("ADMIN")
                    .build();
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password", e);
        }
    }

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
            );

            Optional<User> user = userRepository.findByEmail(authRequestDTO.getEmail());
            if (user.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            String jwt = jwtUtils.generateToken(authRequestDTO.getEmail(), "USER");

            return AuthResponseDTO.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(user.get().getId())
                    .username(user.get().getUsername())
                    .userType("USER")
                    .build();
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password", e);
        }
    }

    public String determineUserType(String email) {
        if (adminRepository.existsByEmail(email)) {
            return "ADMIN";
        } else if (userRepository.existsByEmail(email)) {
            return "USER";
        }
        return null;
    }
}