package com.gateway.fee.infrastructure.persistence.repository;

import com.gateway.fee.infrastructure.persistence.entity.UserMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserMessageRepository extends JpaRepository<UserMessageEntity, UUID> {
}
