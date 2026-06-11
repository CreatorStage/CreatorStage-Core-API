package com.yt.projetos.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yt.projetos.dto.NoteResponse;
import com.yt.projetos.dto.ReferenceResponse;
import com.yt.projetos.dto.ScriptVersionResponse;
import com.yt.projetos.dto.ScriptVersionRequest;
import com.yt.projetos.dto.VideoIdeaResponse;
import com.yt.projetos.dto.VideoIdeaRequest;
import com.yt.projetos.dto.VideoIdeaUpdateRequest;
import com.yt.projetos.dto.VideoScriptResponse;
import com.yt.projetos.dto.VideoScriptRequest;
import com.yt.projetos.model.Note;
import com.yt.projetos.model.Reference;
import com.yt.projetos.model.ScriptVersion;
import com.yt.projetos.model.User;
import com.yt.projetos.model.VideoIdea;
import com.yt.projetos.service.VideoIdeaService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoIdeaController {

    private final VideoIdeaService videoIdeaService;

    // Ideas
    @GetMapping("/channels/{channelId}/ideas")
    public List<VideoIdeaResponse> getIdeas(@AuthenticationPrincipal User currentUser, @PathVariable UUID channelId) {
        return videoIdeaService.getIdeas(currentUser, channelId).stream().map(this::toVideoIdeaResponse).toList();
    }

    @PostMapping("/channels/{channelId}/ideas")
    public ResponseEntity<VideoIdeaResponse> createIdea(@AuthenticationPrincipal User currentUser, @PathVariable UUID channelId, @Valid @RequestBody VideoIdeaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toVideoIdeaResponse(videoIdeaService.createIdea(currentUser, channelId, request)));
    }

    @PutMapping("/ideas/{id}")
    public ResponseEntity<VideoIdeaResponse> updateIdea(@AuthenticationPrincipal User currentUser, @PathVariable UUID id, @Valid @RequestBody VideoIdeaUpdateRequest updates) {
        return ResponseEntity.ok(toVideoIdeaResponse(videoIdeaService.updateIdea(currentUser, id, updates)));
    }

    @DeleteMapping("/ideas/{id}")
    public ResponseEntity<?> deleteIdea(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        videoIdeaService.deleteIdea(currentUser, id);
        return ResponseEntity.ok().build();
    }

    // References
    @GetMapping("/ideas/{ideaId}/references")
    public List<ReferenceResponse> getReferences(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId) {
        return videoIdeaService.getReferences(currentUser, ideaId).stream().map(this::toReferenceResponse).toList();
    }

    @PostMapping("/ideas/{ideaId}/references")
    public ResponseEntity<ReferenceResponse> addReference(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId, @RequestBody Reference reference) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toReferenceResponse(videoIdeaService.addReference(currentUser, ideaId, reference)));
    }

    @DeleteMapping("/references/{id}")
    public ResponseEntity<?> deleteReference(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        videoIdeaService.deleteReference(currentUser, id);
        return ResponseEntity.ok().build();
    }

    // Notes
    @GetMapping("/ideas/{ideaId}/notes")
    public ResponseEntity<NoteResponse> getNotes(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId) {
        return ResponseEntity.ok(toNoteResponse(videoIdeaService.getNotes(currentUser, ideaId)));
    }

    @PutMapping("/ideas/{ideaId}/notes")
    public ResponseEntity<NoteResponse> saveNotes(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId, @RequestBody Note noteUpdate) {
        return ResponseEntity.ok(toNoteResponse(videoIdeaService.saveNotes(currentUser, ideaId, noteUpdate)));
    }

    // Scripts
    @GetMapping("/ideas/{ideaId}/script")
    public ResponseEntity<VideoScriptResponse> getScript(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId) {
        return ResponseEntity.ok(toVideoScriptResponse(videoIdeaService.getScript(currentUser, ideaId)));
    }

    @PutMapping("/ideas/{ideaId}/script")
    public ResponseEntity<VideoScriptResponse> saveScript(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId, @RequestBody VideoScriptRequest scriptUpdate) {
        return ResponseEntity.ok(toVideoScriptResponse(videoIdeaService.saveScript(currentUser, ideaId, scriptUpdate)));
    }

    @GetMapping("/ideas/{ideaId}/script/versions")
    public List<ScriptVersionResponse> getScriptVersions(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId) {
        return videoIdeaService.getScriptVersions(currentUser, ideaId).stream().map(this::toScriptVersionResponse).toList();
    }

    @PostMapping("/ideas/{ideaId}/script/versions")
    public ResponseEntity<ScriptVersionResponse> createScriptVersion(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId, @RequestBody ScriptVersionRequest versionRequest) {
        if (versionRequest == null || versionRequest.getLabel() == null || versionRequest.getLabel().trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da versão não pode ser vazio");
        }
        
        ScriptVersion version = ScriptVersion.builder()
                .label(versionRequest.getLabel().trim())
                .build();
                
        return ResponseEntity.status(HttpStatus.CREATED).body(toScriptVersionResponse(videoIdeaService.createScriptVersion(currentUser, ideaId, version)));
    }

    @PostMapping("/ideas/{ideaId}/script/versions/{versionId}/restore")
    public ResponseEntity<VideoScriptResponse> restoreScriptVersion(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId, @PathVariable UUID versionId) {
        return ResponseEntity.ok(toVideoScriptResponse(videoIdeaService.restoreScriptVersion(currentUser, ideaId, versionId)));
    }

    @DeleteMapping("/ideas/{ideaId}/script/versions/{versionId}")
    public ResponseEntity<?> deleteScriptVersion(@AuthenticationPrincipal User currentUser, @PathVariable UUID ideaId, @PathVariable UUID versionId) {
        videoIdeaService.deleteScriptVersion(currentUser, ideaId, versionId);
        return ResponseEntity.ok().build();
    }

    private VideoIdeaResponse toVideoIdeaResponse(VideoIdea idea) {
        return new VideoIdeaResponse(
                idea.getId(),
                idea.getChannel() != null ? idea.getChannel().getId() : null,
                idea.getMainTitle(),
                idea.getDescription(),
                idea.getStatus(),
                idea.getTags(),
                idea.getAlternativeTitles(),
                idea.getDeadline(),
                idea.getEvergreen(),
                idea.getTrend(),
                idea.getSponsored(),
            idea.getChecklistState(),
            idea.getSponsorBrand(),
            idea.getSponsorDeadline(),
            idea.getSponsorTrackingUrl(),
            idea.getSponsorValue(),
            idea.getSponsorPaymentStatus(),
                idea.getPublishedUrl(),
                idea.getCreatedAt(),
                idea.getUpdatedAt(),
                idea.getDeletedAt()
        );
    }

    private ReferenceResponse toReferenceResponse(Reference reference) {
        return new ReferenceResponse(
                reference.getId(),
                reference.getVideoIdea() != null ? reference.getVideoIdea().getId() : null,
                reference.getType(),
                reference.getUrl(),
                reference.getLabel(),
                reference.getImage() != null ? reference.getImage().getId() : null,
                reference.getCreatedAt()
        );
    }

    private NoteResponse toNoteResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getVideoIdea() != null ? note.getVideoIdea().getId() : null,
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    private VideoScriptResponse toVideoScriptResponse(ScriptVersion script) {
        return new VideoScriptResponse(
                script.getId(),
                script.getVideoIdea() != null ? script.getVideoIdea().getId() : null,
                script.getContent(),
                script.getContentType(),
                script.getWordCount(),
                script.getEstimatedDurationSeconds(),
                script.getUpdatedAt(),
                script.getDeletedAt()
        );
    }

    private ScriptVersionResponse toScriptVersionResponse(ScriptVersion version) {
        return new ScriptVersionResponse(
                version.getId(),
                version.getVideoIdea() != null ? version.getVideoIdea().getId() : null,
                version.getContent(),
                version.getContentType(),
                version.getWordCount(),
                version.getEstimatedDurationSeconds(),
                version.getLabel(),
                version.getCreatedAt()
        );
    }
}
