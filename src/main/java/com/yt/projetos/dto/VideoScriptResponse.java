package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoScriptResponse(
        UUID id,
        UUID videoIdeaId,
        String content,
        String contentType,
        int wordCount,
        int estimatedDurationSeconds,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}