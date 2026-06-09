package com.yt.projetos.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.dto.ChannelReferenceLinkRequest;
import com.yt.projetos.model.Channel;
import com.yt.projetos.model.ChannelReferenceLink;
import com.yt.projetos.model.Reference;
import com.yt.projetos.model.User;
import com.yt.projetos.model.VideoIdea;
import com.yt.projetos.repository.ChannelReferenceLinkRepository;
import com.yt.projetos.repository.ChannelRepository;
import com.yt.projetos.repository.NoteRepository;
import com.yt.projetos.repository.ReferenceRepository;
import com.yt.projetos.repository.ScriptVersionRepository;
import com.yt.projetos.repository.VideoIdeaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final VideoIdeaRepository videoIdeaRepository;
    private final NoteRepository noteRepository;
    private final ReferenceRepository referenceRepository;
    private final ChannelReferenceLinkRepository channelReferenceLinkRepository;
    private final ScriptVersionRepository scriptVersionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuggestionService suggestionService;

    public List<Channel> getChannels(User currentUser) {
        return currentUser == null ? List.of() : channelRepository.findByUserId(currentUser.getId());
    }

    public Channel createChannel(User currentUser, Channel channel) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        channel.setUser(currentUser);
        return channelRepository.save(channel);
    }

    public Channel getChannel(User currentUser, UUID id) {
        return getOwnedChannel(currentUser, id);
    }

    public Channel updateChannel(User currentUser, UUID id, Channel updates) {
        Channel channel = getOwnedChannel(currentUser, id);
        if (updates.getName() != null) channel.setName(updates.getName());
        if (updates.getNiche() != null) channel.setNiche(updates.getNiche());
        if (updates.getCtaTemplates() != null) channel.setCtaTemplates(updates.getCtaTemplates());
        if (updates.getDescriptionBlocks() != null) channel.setDescriptionBlocks(updates.getDescriptionBlocks());
        if (updates.getChecklistTemplates() != null) channel.setChecklistTemplates(updates.getChecklistTemplates());
        return channelRepository.save(channel);
    }

    public List<ChannelReferenceLink> getReferenceLinks(User currentUser, UUID channelId) {
        if (!isOwnedChannel(currentUser, channelId)) {
            return List.of();
        }
        return channelReferenceLinkRepository.findByChannelIdOrderByCreatedAtDesc(channelId);
    }

    public ChannelReferenceLink addReferenceLink(User currentUser, UUID channelId, ChannelReferenceLinkRequest request) {
        Channel channel = getOwnedChannel(currentUser, channelId);
        ChannelReferenceLink link = ChannelReferenceLink.builder()
                .channel(channel)
            .title(request.title())
            .url(request.url())
            .note(request.note())
            .thumbnailUrl(request.thumbnailUrl())
            .type(request.type() != null ? request.type() : com.yt.projetos.model.ReferenceType.LINK)
                .build();
        ChannelReferenceLink saved = channelReferenceLinkRepository.save(link);
        
        if (request.url() != null && (request.url().contains("youtube.com/@") || request.url().contains("youtube.com/channel/") || request.url().contains("youtube.com/c/"))) {
            suggestionService.scrapeSuggestionsForChannel(channel, request.url(), request.title());
        }
        
        return saved;
    }

    public void deleteReferenceLink(User currentUser, UUID id) {
        ChannelReferenceLink link = channelReferenceLinkRepository.findById(id)
                .filter(found -> isOwnedChannel(currentUser, found.getChannel() != null ? found.getChannel().getId() : null))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        channelReferenceLinkRepository.delete(link);
    }

    @Transactional
    public void deleteChannel(User currentUser, UUID id, String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha é obrigatória");
        }

        Channel channel = getOwnedChannel(currentUser, id);
        if (channel.getUser() == null || !passwordEncoder.matches(password, channel.getUser().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }

        List<VideoIdea> ideas = videoIdeaRepository.findByChannelId(id);
        for (VideoIdea idea : ideas) {
            noteRepository.deleteAll(noteRepository.findAllByVideoIdeaIdOrderByCreatedAtAsc(idea.getId()));
            List<Reference> references = referenceRepository.findByVideoIdeaId(idea.getId());
            referenceRepository.deleteAll(references);
            scriptVersionRepository.deleteByVideoIdeaId(idea.getId());
            videoIdeaRepository.delete(idea);
        }

        channelReferenceLinkRepository.deleteByChannelId(id);
        channelRepository.delete(channel);
    }

    private Channel getOwnedChannel(User currentUser, UUID id) {
        if (currentUser == null || id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return channelRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean isOwnedChannel(User currentUser, UUID channelId) {
        if (currentUser == null || channelId == null) {
            return false;
        }
        return channelRepository.existsByIdAndUserId(channelId, currentUser.getId());
    }
}