package com.yt.projetos.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.model.Channel;
import com.yt.projetos.dto.VideoIdeaRequest;
import com.yt.projetos.dto.VideoIdeaUpdateRequest;
import com.yt.projetos.model.Note;
import com.yt.projetos.model.Reference;
import com.yt.projetos.model.ScriptVersion;
import com.yt.projetos.model.User;
import com.yt.projetos.model.VideoIdea;
import com.yt.projetos.model.VideoIdeaStatus;
import com.yt.projetos.repository.ChannelRepository;
import com.yt.projetos.repository.NoteRepository;
import com.yt.projetos.repository.ReferenceRepository;
import com.yt.projetos.repository.ScriptVersionRepository;
import com.yt.projetos.repository.VideoIdeaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoIdeaService {

    private final VideoIdeaRepository videoIdeaRepository;
    private final ChannelRepository channelRepository;
    private final ReferenceRepository referenceRepository;
    private final NoteRepository noteRepository;
    private final ScriptVersionRepository scriptVersionRepository;

    public List<VideoIdea> getIdeas(User currentUser, UUID channelId) {
        if (!isOwnedChannel(currentUser, channelId)) {
            return List.of();
        }
        return videoIdeaRepository.findByChannelId(channelId);
    }

    @Transactional
    public VideoIdea createIdea(User currentUser, UUID channelId, VideoIdeaRequest request) {
        VideoIdea idea = VideoIdea.builder()
                .mainTitle(request.mainTitle())
                .description(request.description())
                .status(request.status() != null ? request.status() : VideoIdeaStatus.IDEA)
                .tags(request.tags() != null ? request.tags() : new java.util.ArrayList<>())
                .alternativeTitles(request.alternativeTitles() != null ? request.alternativeTitles() : new java.util.ArrayList<>())
                .deadline(request.deadline())
                .evergreen(request.evergreen() != null ? request.evergreen() : false)
                .trend(request.trend() != null ? request.trend() : false)
                .checklistState(request.checklistState())
                .publishedUrl(request.publishedUrl())
                .channel(getOwnedChannel(currentUser, channelId))
                .build();
                
        if (Boolean.TRUE.equals(request.sponsored())) {
            idea.setSponsored(true);
            idea.setSponsorBrand(request.sponsorBrand());
            idea.setSponsorDeadline(request.sponsorDeadline());
            idea.setSponsorTrackingUrl(request.sponsorTrackingUrl());
            idea.setSponsorValue(request.sponsorValue());
            idea.setSponsorPaymentStatus(request.sponsorPaymentStatus());
        }
        
        return videoIdeaRepository.save(idea);
    }

    @Transactional
    public VideoIdea updateIdea(User currentUser, UUID id, VideoIdeaUpdateRequest updates) {
        VideoIdea idea = getOwnedIdea(currentUser, id);
        if (updates.mainTitle() != null) {
            if (updates.mainTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título principal não pode ser vazio");
            }
            idea.setMainTitle(updates.mainTitle());
        }
        if (updates.description() != null) idea.setDescription(updates.description());
        if (updates.status() != null) idea.setStatus(updates.status());
        if (updates.tags() != null) idea.setTags(updates.tags());
        if (updates.alternativeTitles() != null) idea.setAlternativeTitles(updates.alternativeTitles());
        if (updates.deadline() != null) idea.setDeadline(updates.deadline());
        if (updates.evergreen() != null) idea.setEvergreen(updates.evergreen());
        if (updates.trend() != null) idea.setTrend(updates.trend());
        if (updates.sponsored() != null) idea.setSponsored(updates.sponsored());
        if (updates.checklistState() != null) idea.setChecklistState(updates.checklistState());
        if (updates.sponsorBrand() != null) idea.setSponsorBrand(updates.sponsorBrand());
        if (updates.sponsorDeadline() != null) idea.setSponsorDeadline(updates.sponsorDeadline());
        if (updates.sponsorTrackingUrl() != null) idea.setSponsorTrackingUrl(updates.sponsorTrackingUrl());
        if (updates.sponsorValue() != null) idea.setSponsorValue(updates.sponsorValue());
        if (updates.sponsorPaymentStatus() != null) idea.setSponsorPaymentStatus(updates.sponsorPaymentStatus());
        if (updates.publishedUrl() != null) idea.setPublishedUrl(updates.publishedUrl());
        return videoIdeaRepository.save(idea);
    }

    @Transactional
    public void deleteIdea(User currentUser, UUID id) {
        getOwnedIdea(currentUser, id);
        noteRepository.deleteAll(noteRepository.findAllByVideoIdeaIdOrderByCreatedAtAsc(id));
        List<Reference> references = referenceRepository.findByVideoIdeaId(id);
        referenceRepository.deleteAll(references);
        scriptVersionRepository.deleteByVideoIdeaId(id);
        videoIdeaRepository.deleteById(id);
    }

    public List<Reference> getReferences(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) return List.of();
        return referenceRepository.findByVideoIdeaId(ideaId);
    }

    @Transactional
    public Reference addReference(User currentUser, UUID ideaId, Reference reference) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        reference.setVideoIdea(idea);
        return referenceRepository.save(reference);
    }

    @Transactional
    public void deleteReference(User currentUser, UUID id) {
        Reference reference = referenceRepository.findById(id)
                .filter(found -> isOwnedIdea(currentUser, found.getVideoIdea() != null ? found.getVideoIdea().getId() : null))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        referenceRepository.deleteById(reference.getId());
    }

    public Note getNotes(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Note note = noteRepository.findFirstByVideoIdeaIdOrderByCreatedAtDesc(ideaId);
        if (note == null) {
            return Note.builder()
                    .videoIdea(getOwnedIdea(currentUser, ideaId))
                    .content("")
                    .build();
        }
        return note;
    }

    @Transactional
    public Note saveNotes(User currentUser, UUID ideaId, Note noteUpdate) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        Note note = Note.builder().videoIdea(idea).content(noteUpdate.getContent()).build();
        return noteRepository.save(note);
    }

    public ScriptVersion getScript(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return scriptVersionRepository.findByVideoIdeaIdAndIsCurrentTrue(ideaId)
                .orElseGet(() -> ScriptVersion.builder()
                        .videoIdea(getOwnedIdea(currentUser, ideaId))
                        .contentType("MARKDOWN")
                        .content("")
                        .label("Draft Inicial")
                        .isCurrent(true)
                        .build());
    }

    @Transactional
    public ScriptVersion saveScript(User currentUser, UUID ideaId, com.yt.projetos.dto.VideoScriptRequest scriptUpdate) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        ScriptVersion script = scriptVersionRepository.findByVideoIdeaIdAndIsCurrentTrue(ideaId)
                .orElse(ScriptVersion.builder().videoIdea(idea).isCurrent(true).label("Draft").build());
        if (script.getId() != null) {
            scriptVersionRepository.save(ScriptVersion.builder()
                    .videoIdea(idea)
                    .content(script.getContent())
                    .contentType(script.getContentType())
                    .wordCount(script.getWordCount())
                    .estimatedDurationSeconds(script.getEstimatedDurationSeconds())
                    .label("Versão automática")
                    .isCurrent(false)
                    .build());
        }
        script.setContent(scriptUpdate.content());
        script.setContentType(scriptUpdate.contentType() != null ? scriptUpdate.contentType() : "text/markdown");
        script.setWordCount(scriptUpdate.wordCount());
        script.setEstimatedDurationSeconds(scriptUpdate.estimatedDurationSeconds());
        script.setUpdatedAt(LocalDateTime.now());
        return scriptVersionRepository.save(script);
    }

    public List<ScriptVersion> getScriptVersions(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) return List.of();
        return scriptVersionRepository.findByVideoIdeaIdOrderByCreatedAtDesc(ideaId).stream()
                .filter(v -> !v.isCurrent() && v.getDeletedAt() == null)
                .toList();
    }

    @Transactional
    public ScriptVersion createScriptVersion(User currentUser, UUID ideaId, ScriptVersion versionRequest) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        ScriptVersion script = scriptVersionRepository.findByVideoIdeaIdAndIsCurrentTrue(ideaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ScriptVersion version = ScriptVersion.builder()
                .videoIdea(idea)
                .content(script.getContent())
                .contentType(script.getContentType())
                .wordCount(script.getWordCount())
                .estimatedDurationSeconds(script.getEstimatedDurationSeconds())
                .label(versionRequest.getLabel() != null ? versionRequest.getLabel() : "Versão manual")
                .isCurrent(false)
                .build();
        return scriptVersionRepository.save(version);
    }

    @Transactional
    public ScriptVersion restoreScriptVersion(User currentUser, UUID ideaId, UUID versionId) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        ScriptVersion version = scriptVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ScriptVersion script = scriptVersionRepository.findByVideoIdeaIdAndIsCurrentTrue(ideaId)
                .orElse(ScriptVersion.builder().videoIdea(idea).isCurrent(true).label("Draft").build());
        
        if (script.getId() != null) {
            scriptVersionRepository.save(ScriptVersion.builder()
                    .videoIdea(idea)
                    .content(script.getContent())
                    .contentType(script.getContentType())
                    .wordCount(script.getWordCount())
                    .estimatedDurationSeconds(script.getEstimatedDurationSeconds())
                    .label("Versão automática")
                    .isCurrent(false)
                    .build());
        }

        script.setContent(version.getContent());
        script.setContentType(version.getContentType() != null ? version.getContentType() : "RICH_TEXT");
        script.setWordCount(version.getWordCount());
        script.setEstimatedDurationSeconds(version.getEstimatedDurationSeconds());
        script.setUpdatedAt(LocalDateTime.now());
        return scriptVersionRepository.save(script);
    }

    @Transactional
    public void deleteScriptVersion(User currentUser, UUID ideaId, UUID versionId) {
        getOwnedIdea(currentUser, ideaId);
        ScriptVersion version = scriptVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        if (!version.getVideoIdea().getId().equals(ideaId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Versão não pertence a esta ideia");
        }
        
        scriptVersionRepository.delete(version);
    }

    private VideoIdea getOwnedIdea(User currentUser, UUID ideaId) {
        if (currentUser == null || ideaId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return videoIdeaRepository.findByIdAndChannelUserId(ideaId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean isOwnedIdea(User currentUser, UUID ideaId) {
        if (currentUser == null || ideaId == null) {
            return false;
        }
        return videoIdeaRepository.existsByIdAndChannelUserId(ideaId, currentUser.getId());
    }

    private boolean isOwnedChannel(User currentUser, UUID channelId) {
        if (currentUser == null || channelId == null) {
            return false;
        }
        return channelRepository.existsByIdAndUserId(channelId, currentUser.getId());
    }

    private Channel getOwnedChannel(User currentUser, UUID channelId) {
        if (currentUser == null || channelId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return channelRepository.findByIdAndUserId(channelId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}