package com.yt.projetos.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.yt.projetos.model.VideoIdeaStatus;

public record VideoIdeaResponse(
        UUID id,
        UUID channelId,
        String mainTitle,
        String description,
        VideoIdeaStatus status,
        List<String> tags,
        List<String> alternativeTitles,
        LocalDateTime deadline,
        Boolean evergreen,
        Boolean trend,
        Boolean sponsored,
        String checklistState,
        String sponsorBrand,
        LocalDateTime sponsorDeadline,
        String sponsorTrackingUrl,
        Double sponsorValue,
        String sponsorPaymentStatus,
        String publishedUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}