package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.UserDTO;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.UserRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} users", users.size());
        return users;
    }

    public UserDTO getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id " + id);
                });
        log.debug("User found with id: {}", id);
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO, String rawPassword) {
        log.info("Creating new user with username: {}", userDTO.getUsername());
        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .telephone(userDTO.getTelephone())
                .build();

        User savedUser = userRepository.save(user);
        log.debug("User created successfully with id: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user with id: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id " + id);
                });

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setTelephone(userDTO.getTelephone());

        User updatedUser = userRepository.save(existingUser);
        log.debug("User updated successfully with id: {}", updatedUser.getId());
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
        log.debug("User deleted successfully with id: {}", id);
    }

    // Helper method to convert user entity to DTO
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .createdAt(user.getCreatedAt())
                .build();
    }
}