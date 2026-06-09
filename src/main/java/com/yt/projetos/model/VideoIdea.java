package com.yt.projetos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
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
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "alternative_titles", columnDefinition = "jsonb")
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

    @Column(name = "checklist_state", columnDefinition = "TEXT")
    private String checklistState;

    private String publishedUrl;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "videoIdea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private VideoIdeaSponsorship sponsorship;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Boolean getSponsored() {
        return sponsorship != null ? sponsorship.isSponsored() : null;
    }

    public void setSponsored(Boolean sponsored) {
        ensureSponsorshipExists();
        sponsorship.setSponsored(Boolean.TRUE.equals(sponsored));
    }

    public String getSponsorBrand() {
        return sponsorship != null ? sponsorship.getBrand() : null;
    }

    public void setSponsorBrand(String brand) {
        ensureSponsorshipExists();
        sponsorship.setBrand(brand);
    }

    public LocalDateTime getSponsorDeadline() {
        return sponsorship != null ? sponsorship.getDeadline() : null;
    }

    public void setSponsorDeadline(LocalDateTime deadline) {
        ensureSponsorshipExists();
        sponsorship.setDeadline(deadline);
    }

    public String getSponsorTrackingUrl() {
        return sponsorship != null ? sponsorship.getTrackingUrl() : null;
    }

    public void setSponsorTrackingUrl(String trackingUrl) {
        ensureSponsorshipExists();
        sponsorship.setTrackingUrl(trackingUrl);
    }

    public Double getSponsorValue() {
        return sponsorship != null && sponsorship.getValue() != null ? sponsorship.getValue().doubleValue() : null;
    }

    public void setSponsorValue(Double value) {
        ensureSponsorshipExists();
        sponsorship.setValue(value != null ? java.math.BigDecimal.valueOf(value) : null);
    }

    public String getSponsorPaymentStatus() {
        return sponsorship != null ? sponsorship.getPaymentStatus() : null;
    }

    public void setSponsorPaymentStatus(String paymentStatus) {
        ensureSponsorshipExists();
        sponsorship.setPaymentStatus(paymentStatus);
    }

    private void ensureSponsorshipExists() {
        if (sponsorship == null) {
            sponsorship = VideoIdeaSponsorship.builder()
                    .videoIdea(this)
                    .sponsored(false)
                    .build();
        }
    }
}

