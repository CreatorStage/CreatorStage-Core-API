package com.yt.projetos.dto;

import jakarta.validation.constraints.NotBlank;

public record UserSettingsRequest(
        @NotBlank(message = "O tema não pode ser vazio")
        String theme,
        
        @NotBlank(message = "O idioma preferido não pode ser vazio")
        String preferredLanguage,
        
        String profileBio
) {}
