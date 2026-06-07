package com.yt.projetos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.ChannelReferenceLink;

@Repository
public interface ChannelReferenceLinkRepository extends JpaRepository<ChannelReferenceLink, UUID> {
    List<ChannelReferenceLink> findByChannelIdOrderByCreatedAtDesc(UUID channelId);

    void deleteByChannelId(UUID channelId);
}