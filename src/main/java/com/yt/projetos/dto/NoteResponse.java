package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        UUID videoIdeaId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}