package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.yt.projetos.model.ReferenceType;

public record ChannelReferenceLinkResponse(
        UUID id,
        UUID channelId,
        String title,
        String url,
        String note,
        String thumbnailUrl,
        ReferenceType type,
        LocalDateTime createdAt
) {
}