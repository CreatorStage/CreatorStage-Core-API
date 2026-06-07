package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        UUID channelId,
        String title,
        String description,
        Double targetValue,
        Double currentValue,
        LocalDateTime deadline,
        boolean completed,
        LocalDateTime createdAt
) {
}