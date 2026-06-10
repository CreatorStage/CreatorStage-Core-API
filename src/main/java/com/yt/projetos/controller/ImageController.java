package com.yt.projetos.controller;

import com.yt.projetos.dto.ImageUploadRequest;
import com.yt.projetos.dto.UploadResponse;
import com.yt.projetos.model.UploadedImage;
import com.yt.projetos.model.User;
import com.yt.projetos.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadImage(@AuthenticationPrincipal User currentUser, @RequestBody ImageUploadRequest request) {
        return ResponseEntity.ok(new UploadResponse(imageService.uploadImage(currentUser, request)));
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        UploadedImage image = imageService.getImage(currentUser, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, image.getContentType())
                .body(image.getData());
    }
}
