package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        LocalDateTime createdAt
) {
}