package com.yt.projetos.dto;

public record VideoScriptRequest(
    String content,
    String contentType,
    int wordCount,
    int estimatedDurationSeconds
) {}
