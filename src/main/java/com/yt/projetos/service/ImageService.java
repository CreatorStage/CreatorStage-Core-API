package com.yt.projetos.service;

import java.util.Base64;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.dto.ImageUploadRequest;
import com.yt.projetos.model.UploadedImage;
import com.yt.projetos.model.User;
import com.yt.projetos.repository.UploadedImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final UploadedImageRepository uploadedImageRepository;

    public String uploadImage(User currentUser, ImageUploadRequest request) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        try {
            String base64Data = request.imageBase64();
            if (base64Data == null || base64Data.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da imagem vazios");
            }

            // Limit Base64 length to prevent Heap Exhaustion.
            // 5MB of binary data is ~6.7MB in Base64 encoding. Max 10MB Base64 string allowed (~7.5MB file)
            if (base64Data.length() > 10 * 1024 * 1024) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Imagem muito grande (máximo 7.5MB)");
            }

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
                    .user(currentUser)
                    .build();

            UploadedImage saved = uploadedImageRepository.save(image);
            return "/api/images/" + saved.getId();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha no upload: " + e.getMessage());
        }
    }

    public UploadedImage getImage(User currentUser, UUID id) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        UploadedImage image = uploadedImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (image.getUser() == null || !image.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado a esta imagem");
        }

        return image;
    }
}