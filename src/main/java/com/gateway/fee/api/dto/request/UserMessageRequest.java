package com.gateway.fee.api.dto.request;

import java.time.LocalDateTime;

public record UserMessageRequest(String name,String email, LocalDateTime timestamp) {}