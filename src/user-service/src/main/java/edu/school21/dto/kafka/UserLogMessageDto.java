package edu.school21.dto.kafka;

import java.time.LocalDateTime;

public record UserLogMessageDto(Long id,
                                String username,
                                Long collectionId,
                                LocalDateTime createdAt,
                                String message) {
}
