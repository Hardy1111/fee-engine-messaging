package com.gateway.producer.producer_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank String name,
        @NotBlank @Email String email
) {
}