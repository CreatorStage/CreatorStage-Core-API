package com.yt.projetos.dto;

public record ImageUploadRequest(
        String imageBase64,
        String filename
) {
}
