package com.yt.projetos.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.yt.projetos.dto.UserSettingsResponse;
import com.yt.projetos.model.UserSettings;
import com.yt.projetos.model.User;
import com.yt.projetos.service.SettingsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserSettingsResponse> getSettings(@AuthenticationPrincipal User currentUser, @PathVariable UUID userId) {
        return ResponseEntity.ok(toSettingsResponse(settingsService.getSettings(currentUser, userId)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserSettingsResponse> updateSettings(@AuthenticationPrincipal User currentUser, @PathVariable UUID userId, @RequestBody UserSettings updates) {
        return ResponseEntity.ok(toSettingsResponse(settingsService.updateSettings(currentUser, userId, updates)));
    }

    private UserSettingsResponse toSettingsResponse(UserSettings settings) {
        return new UserSettingsResponse(
                settings.getUserId(),
                settings.getTheme(),
                settings.isEmailNotifications(),
                settings.getPreferredLanguage(),
                settings.getProfileBio()
        );
    }
}
