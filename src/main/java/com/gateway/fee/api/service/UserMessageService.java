package com.gateway.fee.api.service;

import com.gateway.fee.api.dto.response.UserMessageResponse;
import com.gateway.fee.infrastructure.persistence.entity.UserMessageEntity;
import com.gateway.fee.infrastructure.persistence.repository.UserMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//def of class: fetch from the database, convert to DTOs, hand them back to the controller.
@Service
@RequiredArgsConstructor
public class UserMessageService {

    private final UserMessageRepository repository;

    // returns a paginated page of stored messages, converted to response DTOs
    public Page<UserMessageResponse> getAllMessages(Pageable pageable) {
        return repository.findAll(pageable).map(entity -> toResponse(entity));
    }

    // private helper: converts ONE entity into ONE response DTO
    private UserMessageResponse toResponse(UserMessageEntity entity) {
        return new UserMessageResponse(
                entity.getName(),
                entity.getEmail(),
                entity.getSentAt()
        );
    }
}