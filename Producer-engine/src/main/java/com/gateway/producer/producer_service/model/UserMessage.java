package com.gateway.producer.producer_service.model;

import java.time.LocalDateTime;

public record UserMessage(String name, String email, LocalDateTime timestamp) {

}
