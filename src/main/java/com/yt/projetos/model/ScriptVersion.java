package com.yt.projetos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "script_versions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScriptVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_idea_id", nullable = false)
    private VideoIdea videoIdea;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String contentType;
    private int wordCount;
    private int estimatedDurationSeconds;

    @Column(nullable = false)
    private String label;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
