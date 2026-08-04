package com.gateway.fee.infrastructure.listener;


import com.gateway.fee.api.dto.request.UserMessageRequest;
import com.gateway.fee.infrastructure.persistence.entity.UserMessageEntity;
import com.gateway.fee.infrastructure.persistence.repository.UserMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserMessageListener {

    private final UserMessageRepository repository;

    @RabbitListener(queues = "user.queue")
    public void receiveMessage(UserMessageRequest message) {
        log.info("Received message: {}", message);

        UserMessageEntity entity = UserMessageEntity.builder()
               .name(message.name())
               .email(message.email())
               .sentAt(message.timestamp())
               .build();

        repository.save(entity);
    }
}