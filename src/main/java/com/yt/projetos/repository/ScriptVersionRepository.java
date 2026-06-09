package com.yt.projetos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.ScriptVersion;

@Repository
public interface ScriptVersionRepository extends JpaRepository<ScriptVersion, UUID> {
    List<ScriptVersion> findByVideoIdeaIdOrderByCreatedAtDesc(UUID videoIdeaId);
    void deleteByVideoIdeaId(UUID videoIdeaId);
    java.util.Optional<ScriptVersion> findByVideoIdeaIdAndIsCurrentTrue(UUID videoIdeaId);
}
