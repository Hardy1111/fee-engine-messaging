package com.gateway.fee.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}