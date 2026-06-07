package com.yt.projetos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "video_references")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_idea_id", nullable = false)
    private VideoIdea videoIdea;

    @Column(nullable = false)
    private String type; // LINK or IMAGE

    @Column(columnDefinition = "TEXT")
    private String url; // External URL for LINK, or internal reference for IMAGE

    @Column(nullable = false)
    private String label;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private UploadedImage image;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
