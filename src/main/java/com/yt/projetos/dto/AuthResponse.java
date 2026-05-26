package com.yt.projetos.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
