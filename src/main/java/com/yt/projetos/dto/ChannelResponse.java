package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        UUID userId,
        String name,
        String niche,
        List<String> ctaTemplates,
        String descriptionBlocks,
        String checklistTemplates,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}