package com.gateway.fee.api.controller;

import com.gateway.fee.api.dto.response.UserMessageResponse;
import com.gateway.fee.api.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserMessageController {

    private final UserMessageService userMessageService;

    @GetMapping("/messages")
    public ResponseEntity<Page<UserMessageResponse>> getMessages(Pageable pageable) {
        return ResponseEntity.ok(userMessageService.getAllMessages(pageable));
    }
}