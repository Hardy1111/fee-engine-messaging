package com.gateway.producer.producer_service.service;

import com.gateway.producer.producer_service.model.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    public UserMessage sendMessage(String name, String email) {

        UserMessage message = new UserMessage(name, email, LocalDateTime.now());
        rabbitTemplate.convertAndSend("user.exchange", "user.routingkey", message);
        log.info("Sent message: {}", message);
        return message;

    }
}