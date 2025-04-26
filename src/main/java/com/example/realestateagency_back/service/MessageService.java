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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id " + id));
        return convertToDTO(message);
    }

    public List<MessageDTO> getMessagesByUserId(Long userId) {
        return messageRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getMessagesByPropertyId(Long propertyId) {
        return messageRepository.findByPropertyIdOrderBySentDateDesc(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO createMessage(MessageDTO messageDTO) {
        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + messageDTO.getUserId()));

        Property property = propertyRepository.findById(messageDTO.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + messageDTO.getPropertyId()));

        Message message = Message.builder()
                .content(messageDTO.getContent())
                .user(user)
                .property(property)
                .sentDate(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    @Transactional
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message not found with id " + id);
        }
        messageRepository.deleteById(id);
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
}