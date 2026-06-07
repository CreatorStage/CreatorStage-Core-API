package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReferenceResponse(
        UUID id,
        UUID videoIdeaId,
        String type,
        String url,
        String label,
        UUID imageId,
        LocalDateTime createdAt
) {
}