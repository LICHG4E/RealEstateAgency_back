package com.example.realestateagency_back.service;

import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.repository.AdminRepository;
import com.example.realestateagency_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details by email: {}", email);
        // First try to find admin by email
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            log.debug("Found admin with email: {}", email);
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    new ArrayList<>()
            );
        }

        // Then try to find user by email
        log.debug("Admin not found, searching for regular user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("No user found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("Found regular user with email: {}", email);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public UserDetails loadUserByEmail(String email, String userType) throws UsernameNotFoundException {
        log.debug("Loading user details by email: {} and user type: {}", email, userType);
        if ("ADMIN".equals(userType)) {
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("Admin not found with email: {}", email);
                        return new UsernameNotFoundException("Admin not found with email: " + email);
                    });

            log.debug("Found admin with email: {}", email);
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    new ArrayList<>()
            );
        } else {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });

            log.debug("Found regular user with email: {}", email);
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    new ArrayList<>()
            );
        }
    }
}