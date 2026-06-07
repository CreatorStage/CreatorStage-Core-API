package com.yt.projetos.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.model.User;
import com.yt.projetos.model.UserSettings;
import com.yt.projetos.repository.UserRepository;
import com.yt.projetos.repository.UserSettingsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public UserSettings getSettings(User currentUser, UUID userId) {
        ensureOwner(currentUser, userId);

        return userSettingsRepository.findById(userId)
                .orElseGet(() -> userRepository.findById(userId)
                        .map(user -> userSettingsRepository.save(UserSettings.builder()
                                .user(user)
                                .userId(userId)
                                .theme("dark")
                                .emailNotifications(true)
                                .preferredLanguage("pt-BR")
                                .build()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public UserSettings updateSettings(User currentUser, UUID userId, UserSettings updates) {
        ensureOwner(currentUser, userId);

        UserSettings settings = userSettingsRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (updates.getTheme() != null) {
            settings.setTheme(updates.getTheme());
        }
        settings.setEmailNotifications(updates.isEmailNotifications());
        if (updates.getPreferredLanguage() != null) {
            settings.setPreferredLanguage(updates.getPreferredLanguage());
        }
        if (updates.getProfileBio() != null) {
            settings.setProfileBio(updates.getProfileBio());
        }
        return userSettingsRepository.save(settings);
    }

    private void ensureOwner(User currentUser, UUID userId) {
        if (currentUser == null || !currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}