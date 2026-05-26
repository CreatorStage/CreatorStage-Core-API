package com.yt.projetos.dto;

import java.util.UUID;

public record UserSettingsResponse(
        UUID userId,
        String theme,
        boolean emailNotifications,
        String preferredLanguage,
        String profileBio
) {
}