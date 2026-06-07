package com.yt.projetos.service;

import java.util.Base64;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.dto.ImageUploadRequest;
import com.yt.projetos.model.UploadedImage;
import com.yt.projetos.repository.UploadedImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final UploadedImageRepository uploadedImageRepository;

    public String uploadImage(ImageUploadRequest request) {
        try {
            String base64Data = request.imageBase64();
            String contentType = "image/png";
            if (base64Data.contains(",")) {
                contentType = base64Data.substring(base64Data.indexOf(":") + 1, base64Data.indexOf(";"));
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            UploadedImage image = UploadedImage.builder()
                    .filename(request.filename())
                    .contentType(contentType)
                    .data(decodedBytes)
                    .build();

            UploadedImage saved = uploadedImageRepository.save(image);
            return "/api/images/" + saved.getId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha no upload: " + e.getMessage());
        }
    }

    public UploadedImage getImage(UUID id) {
        return uploadedImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}