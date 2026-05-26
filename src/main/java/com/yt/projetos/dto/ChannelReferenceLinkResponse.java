package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelReferenceLinkResponse(
        UUID id,
        UUID channelId,
        String title,
        String url,
        String note,
        LocalDateTime createdAt
) {
}