package com.yt.projetos.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
