package com.yt.projetos.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.VideoScript;

@Repository
public interface VideoScriptRepository extends JpaRepository<VideoScript, UUID> {
    Optional<VideoScript> findByVideoIdeaId(UUID videoIdeaId);
}
