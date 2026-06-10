package com.yt.projetos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record GoalRequest(
        @NotBlank(message = "O título do objetivo não pode ser vazio")
        String title,
        
        String description,
        
        @NotNull(message = "O valor alvo não pode ser nulo")
        Double targetValue,
        
        Double currentValue,
        LocalDateTime deadline,
        boolean completed
) {}
