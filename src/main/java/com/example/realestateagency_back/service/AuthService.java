package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.AuthRequestDTO;
import com.example.realestateagency_back.dto.AuthResponseDTO;
import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.repository.AdminRepository;
import com.example.realestateagency_back.repository.UserRepository;
import com.example.realestateagency_back.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AuthResponseDTO authenticateAdmin(AuthRequestDTO authRequestDTO) {
        log.info("Authenticating admin with email: {}", authRequestDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
            );

            Optional<Admin> admin = adminRepository.findByEmail(authRequestDTO.getEmail());
            if (admin.isEmpty()) {
                log.error("Admin not found with email: {}", authRequestDTO.getEmail());
                throw new RuntimeException("Admin not found");
            }

            String jwt = jwtUtils.generateToken(authRequestDTO.getEmail(), "ADMIN");
            log.debug("Admin authenticated successfully: {}", admin.get().getUsername());

            return AuthResponseDTO.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(admin.get().getId())
                    .username(admin.get().getUsername())
                    .userType("ADMIN")
                    .build();
        } catch (AuthenticationException e) {
            log.error("Authentication failed for admin with email: {}", authRequestDTO.getEmail(), e);
            throw new RuntimeException("Invalid email or password", e);
        }
    }

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO) {
        log.info("Authenticating user with email: {}", authRequestDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
            );

            Optional<User> user = userRepository.findByEmail(authRequestDTO.getEmail());
            if (user.isEmpty()) {
                log.error("User not found with email: {}", authRequestDTO.getEmail());
                throw new RuntimeException("User not found");
            }

            String jwt = jwtUtils.generateToken(authRequestDTO.getEmail(), "USER");
            log.debug("User authenticated successfully: {}", user.get().getUsername());

            return AuthResponseDTO.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(user.get().getId())
                    .username(user.get().getUsername())
                    .userType("USER")
                    .build();
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user with email: {}", authRequestDTO.getEmail(), e);
            throw new RuntimeException("Invalid email or password", e);
        }
    }

    public String determineUserType(String email) {
        log.debug("Determining user type for email: {}", email);
        if (adminRepository.existsByEmail(email)) {
            log.debug("User type: ADMIN for email: {}", email);
            return "ADMIN";
        } else if (userRepository.existsByEmail(email)) {
            log.debug("User type: USER for email: {}", email);
            return "USER";
        }
        log.debug("No user found for email: {}", email);
        return null;
    }
}