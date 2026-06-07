package com.yt.projetos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findAllByVideoIdeaIdOrderByCreatedAtAsc(UUID videoIdeaId);
    Note findFirstByVideoIdeaIdOrderByCreatedAtDesc(UUID videoIdeaId);
}
