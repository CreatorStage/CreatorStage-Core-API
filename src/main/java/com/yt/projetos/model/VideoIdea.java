package com.yt.projetos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "video_ideas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoIdea {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private String mainTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoIdeaStatus status;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "video_idea_tags", joinColumns = @JoinColumn(name = "video_idea_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "video_idea_alternative_titles", joinColumns = @JoinColumn(name = "video_idea_id"))
    @Column(name = "alternative_title")
    private List<String> alternativeTitles = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "videoIdea", fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Note> notes = new ArrayList<>();

    private LocalDateTime deadline;

    private Boolean evergreen;
    private Boolean trend;
    private Boolean sponsored;

    @Column(name = "checklist_state", columnDefinition = "TEXT")
    private String checklistState;

    @Column(name = "sponsor_brand")
    private String sponsorBrand;

    @Column(name = "sponsor_deadline")
    private LocalDateTime sponsorDeadline;

    @Column(name = "sponsor_tracking_url")
    private String sponsorTrackingUrl;

    @Column(name = "sponsor_value")
    private Double sponsorValue;

    @Column(name = "sponsor_payment_status")
    private String sponsorPaymentStatus;

    private String publishedUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
