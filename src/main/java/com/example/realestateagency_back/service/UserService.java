package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.UserDTO;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO, String rawPassword) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .telephone(userDTO.getTelephone())
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setTelephone(userDTO.getTelephone());

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
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