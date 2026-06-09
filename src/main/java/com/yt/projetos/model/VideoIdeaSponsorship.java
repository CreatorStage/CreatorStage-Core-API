package com.yt.projetos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "video_idea_sponsorships")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoIdeaSponsorship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_idea_id", nullable = false)
    private VideoIdea videoIdea;

    private String brand;
    private LocalDateTime deadline;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "tracking_url", columnDefinition = "TEXT")
    private String trackingUrl;

    private BigDecimal value;

    @Column(nullable = false)
    @Builder.Default
    private boolean sponsored = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
