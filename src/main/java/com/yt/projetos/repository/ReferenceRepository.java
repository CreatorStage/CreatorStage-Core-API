package com.yt.projetos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.Reference;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, UUID> {
    List<Reference> findByVideoIdeaId(UUID videoIdeaId);
}
