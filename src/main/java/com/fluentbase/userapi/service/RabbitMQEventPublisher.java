package com.fluentbase.userapi.service;

import com.fluentbase.userapi.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserDeletedEvent(UUID userId, String email) {
        Map<String, Object> event = Map.of(
                "userId", userId.toString(),
                "email", email,
                "deletedAt", LocalDateTime.now().toString()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EVENTS_EXCHANGE,
                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                event
        );

        log.info("Published user.deleted event for userId={}", userId);
    }
}
