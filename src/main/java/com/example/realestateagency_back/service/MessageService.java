package com.example.realestateagency_back.service;

import com.example.realestateagency_back.dto.MessageDTO;
import com.example.realestateagency_back.entity.Message;
import com.example.realestateagency_back.entity.Property;
import com.example.realestateagency_back.entity.User;
import com.example.realestateagency_back.exception.ResourceNotFoundException;
import com.example.realestateagency_back.repository.MessageRepository;
import com.example.realestateagency_back.repository.PropertyRepository;
import com.example.realestateagency_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public List<MessageDTO> getAllMessages() {
        log.info("Fetching all messages");
        return messageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        log.info("Fetching message with id: {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Message not found with id: {}", id);
                    return new ResourceNotFoundException("Message not found with id " + id);
                });
        return convertToDTO(message);
    }

    public List<MessageDTO> getMessagesByUserId(Long userId) {
        log.info("Fetching messages for user with id: {}", userId);
        return messageRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getMessagesByPropertyId(Long propertyId) {
        log.info("Fetching messages for property with id: {}", propertyId);
        return messageRepository.findByPropertyIdOrderBySentDateDesc(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO createMessage(MessageDTO messageDTO) {
        log.info("Creating message for user: {} and property: {}", messageDTO.getUserId(), messageDTO.getPropertyId());
        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", messageDTO.getUserId());
                    return new ResourceNotFoundException("User not found with id " + messageDTO.getUserId());
                });

        Property property = propertyRepository.findById(messageDTO.getPropertyId())
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", messageDTO.getPropertyId());
                    return new ResourceNotFoundException("Property not found with id " + messageDTO.getPropertyId());
                });

        Message message = Message.builder()
                .content(messageDTO.getContent())
                .user(user)
                .property(property)
                .sentDate(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        log.debug("Message created successfully with id: {}", savedMessage.getId());
        return convertToDTO(savedMessage);
    }

    @Transactional
    public void deleteMessage(Long id) {
        log.info("Deleting message with id: {}", id);
        if (!messageRepository.existsById(id)) {
            log.error("Message not found with id: {}", id);
            throw new ResourceNotFoundException("Message not found with id " + id);
        }
        messageRepository.deleteById(id);
        log.debug("Message deleted successfully with id: {}", id);
    }

    // Helper method to convert Message entity to DTO
    private MessageDTO convertToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .userId(message.getUser().getId())
                .username(message.getUser().getUsername())
                .propertyId(message.getProperty().getId())
                .propertyLocation(message.getProperty().getLocation())
                .sentDate(message.getSentDate())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public List<MessageDTO> getMessagesByUserAndPropertyId(Long userId, Long propertyId) {
        log.info("Fetching messages for user: {} and property: {}", userId, propertyId);
        return messageRepository.findByUserIdAndPropertyId(userId, propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}