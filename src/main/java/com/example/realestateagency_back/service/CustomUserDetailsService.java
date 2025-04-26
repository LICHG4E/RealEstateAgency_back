package com.example.realestateagency_back.service;

import com.example.realestateagency_back.entity.Admin;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.repository.AdminRepository;
import com.example.realestateagency_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find admin
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    new ArrayList<>()
            );
        }

        // Then try to find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public UserDetails loadUserByUsername(String username, String userType) throws UsernameNotFoundException {
        if ("ADMIN".equals(userType)) {
            Admin admin = adminRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));

            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    new ArrayList<>()
            );
        } else {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    new ArrayList<>()
            );
        }
    }
}