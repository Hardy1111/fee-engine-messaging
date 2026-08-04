package com.gateway.producer.producer_service.controller;

import com.gateway.producer.producer_service.dto.request.SendMessageRequest;
import com.gateway.producer.producer_service.model.UserMessage;
import com.gateway.producer.producer_service.service.MessageProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducerService messageProducerService;

    @PostMapping("/send")
    public ResponseEntity<UserMessage> send(@Valid @RequestBody SendMessageRequest request) {
        UserMessage message = messageProducerService.sendMessage(request.name(), request.email());
        return ResponseEntity.ok(message);



    }
}