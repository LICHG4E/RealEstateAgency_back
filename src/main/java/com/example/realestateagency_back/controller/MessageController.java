package com.example.realestateagency_back.controller;

import com.example.realestateagency_back.dto.MessageDTO;
import com.example.realestateagency_back.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        List<MessageDTO> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        MessageDTO message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByUserId(@PathVariable Long userId) {
        List<MessageDTO> messages = messageService.getMessagesByUserId(userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByPropertyId(@PathVariable Long propertyId) {
        List<MessageDTO> messages = messageService.getMessagesByPropertyId(propertyId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageDTO messageDTO) {
        MessageDTO createdMessage = messageService.createMessage(messageDTO);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}