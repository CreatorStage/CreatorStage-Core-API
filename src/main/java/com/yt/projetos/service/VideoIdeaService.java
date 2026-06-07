package com.yt.projetos.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.model.Channel;
import com.yt.projetos.model.Note;
import com.yt.projetos.model.Reference;
import com.yt.projetos.model.ScriptVersion;
import com.yt.projetos.model.User;
import com.yt.projetos.model.VideoIdea;
import com.yt.projetos.model.VideoIdeaStatus;
import com.yt.projetos.model.VideoScript;
import com.yt.projetos.repository.ChannelRepository;
import com.yt.projetos.repository.NoteRepository;
import com.yt.projetos.repository.ReferenceRepository;
import com.yt.projetos.repository.ScriptVersionRepository;
import com.yt.projetos.repository.VideoIdeaRepository;
import com.yt.projetos.repository.VideoScriptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoIdeaService {

    private final VideoIdeaRepository videoIdeaRepository;
    private final ChannelRepository channelRepository;
    private final ReferenceRepository referenceRepository;
    private final NoteRepository noteRepository;
    private final VideoScriptRepository videoScriptRepository;
    private final ScriptVersionRepository scriptVersionRepository;

    public List<VideoIdea> getIdeas(User currentUser, UUID channelId) {
        if (!isOwnedChannel(currentUser, channelId)) {
            return List.of();
        }
        return videoIdeaRepository.findByChannelId(channelId);
    }

    public VideoIdea createIdea(User currentUser, UUID channelId, VideoIdea idea) {
        idea.setChannel(getOwnedChannel(currentUser, channelId));
        idea.setStatus(VideoIdeaStatus.IDEA);
        return videoIdeaRepository.save(idea);
    }

    public VideoIdea updateIdea(User currentUser, UUID id, VideoIdea updates) {
        VideoIdea idea = getOwnedIdea(currentUser, id);
        if (updates.getMainTitle() != null) idea.setMainTitle(updates.getMainTitle());
        if (updates.getDescription() != null) idea.setDescription(updates.getDescription());
        if (updates.getStatus() != null) idea.setStatus(updates.getStatus());
        if (updates.getTags() != null) idea.setTags(updates.getTags());
        if (updates.getAlternativeTitles() != null) idea.setAlternativeTitles(updates.getAlternativeTitles());
        if (updates.getDeadline() != null) idea.setDeadline(updates.getDeadline());
        if (updates.getEvergreen() != null) idea.setEvergreen(updates.getEvergreen());
        if (updates.getTrend() != null) idea.setTrend(updates.getTrend());
        if (updates.getSponsored() != null) idea.setSponsored(updates.getSponsored());
        if (updates.getChecklistState() != null) idea.setChecklistState(updates.getChecklistState());
        if (updates.getSponsorBrand() != null) idea.setSponsorBrand(updates.getSponsorBrand());
        if (updates.getSponsorDeadline() != null) idea.setSponsorDeadline(updates.getSponsorDeadline());
        if (updates.getSponsorTrackingUrl() != null) idea.setSponsorTrackingUrl(updates.getSponsorTrackingUrl());
        if (updates.getSponsorValue() != null) idea.setSponsorValue(updates.getSponsorValue());
        if (updates.getSponsorPaymentStatus() != null) idea.setSponsorPaymentStatus(updates.getSponsorPaymentStatus());
        if (updates.getPublishedUrl() != null) idea.setPublishedUrl(updates.getPublishedUrl());
        return videoIdeaRepository.save(idea);
    }

    @Transactional
    public void deleteIdea(User currentUser, UUID id) {
        getOwnedIdea(currentUser, id);
        noteRepository.deleteAll(noteRepository.findAllByVideoIdeaIdOrderByCreatedAtAsc(id));
        List<Reference> references = referenceRepository.findByVideoIdeaId(id);
        referenceRepository.deleteAll(references);
        videoScriptRepository.findByVideoIdeaId(id).ifPresent(videoScriptRepository::delete);
        scriptVersionRepository.deleteByVideoIdeaId(id);
        videoIdeaRepository.deleteById(id);
    }

    public List<Reference> getReferences(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) return List.of();
        return referenceRepository.findByVideoIdeaId(ideaId);
    }

    public Reference addReference(User currentUser, UUID ideaId, Reference reference) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        reference.setVideoIdea(idea);
        return referenceRepository.save(reference);
    }

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

    public Note saveNotes(User currentUser, UUID ideaId, Note noteUpdate) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        Note note = Note.builder().videoIdea(idea).content(noteUpdate.getContent()).build();
        return noteRepository.save(note);
    }

    public VideoScript getScript(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return videoScriptRepository.findByVideoIdeaId(ideaId)
                .orElseGet(() -> VideoScript.builder()
                        .videoIdea(getOwnedIdea(currentUser, ideaId))
                        .contentType("MARKDOWN")
                        .content("")
                        .build());
    }

    public VideoScript saveScript(User currentUser, UUID ideaId, VideoScript scriptUpdate) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        VideoScript script = videoScriptRepository.findByVideoIdeaId(ideaId)
                .orElse(VideoScript.builder().videoIdea(idea).build());
        if (script.getId() != null) {
            scriptVersionRepository.save(ScriptVersion.builder()
                    .videoIdea(idea)
                    .content(script.getContent())
                    .contentType(script.getContentType())
                    .wordCount(script.getWordCount())
                    .estimatedDurationSeconds(script.getEstimatedDurationSeconds())
                    .label("Versão automática")
                    .build());
        }
        script.setContent(scriptUpdate.getContent());
        script.setContentType(scriptUpdate.getContentType() != null ? scriptUpdate.getContentType() : "text/markdown");
        script.setWordCount(scriptUpdate.getWordCount());
        script.setEstimatedDurationSeconds(scriptUpdate.getEstimatedDurationSeconds());
        return videoScriptRepository.save(script);
    }

    public List<ScriptVersion> getScriptVersions(User currentUser, UUID ideaId) {
        if (!isOwnedIdea(currentUser, ideaId)) return List.of();
        return scriptVersionRepository.findByVideoIdeaIdOrderByCreatedAtDesc(ideaId);
    }

    public ScriptVersion createScriptVersion(User currentUser, UUID ideaId, ScriptVersion versionRequest) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        VideoScript script = videoScriptRepository.findByVideoIdeaId(ideaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ScriptVersion version = ScriptVersion.builder()
                .videoIdea(idea)
                .content(script.getContent())
                .contentType(script.getContentType())
                .wordCount(script.getWordCount())
                .estimatedDurationSeconds(script.getEstimatedDurationSeconds())
                .label(versionRequest.getLabel() != null ? versionRequest.getLabel() : "Versão manual")
                .build();
        return scriptVersionRepository.save(version);
    }

    public VideoScript restoreScriptVersion(User currentUser, UUID ideaId, UUID versionId) {
        VideoIdea idea = getOwnedIdea(currentUser, ideaId);
        ScriptVersion version = scriptVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        VideoScript script = videoScriptRepository.findByVideoIdeaId(ideaId)
                .orElse(VideoScript.builder().videoIdea(idea).build());
        script.setContent(version.getContent());
        script.setContentType(version.getContentType() != null ? version.getContentType() : "RICH_TEXT");
        script.setWordCount(version.getWordCount());
        script.setEstimatedDurationSeconds(version.getEstimatedDurationSeconds());
        return videoScriptRepository.save(script);
    }

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