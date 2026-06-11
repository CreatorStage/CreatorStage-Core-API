package com.yt.projetos.dto;

import com.yt.projetos.model.VideoIdeaStatus;
import java.time.LocalDateTime;
import java.util.List;

public record VideoIdeaUpdateRequest(
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
        String publishedUrl
) {}
