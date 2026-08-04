package com.gateway.fee.api.dto.response;

import java.time.LocalDateTime;

public record UserMessageResponse(String name, String email, LocalDateTime sentAt) {
}