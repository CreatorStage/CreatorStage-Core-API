package com.yt.projetos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
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

@Entity
@Table(name = "channels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String niche;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "cta_templates", columnDefinition = "TEXT")
    private List<String> ctaTemplates = new ArrayList<>();

    @Column(name = "description_blocks", columnDefinition = "TEXT")
    private String descriptionBlocks;

    @Column(name = "checklist_templates", columnDefinition = "TEXT")
    private String checklistTemplates;

    @org.hibernate.annotations.CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Converter
    public static class StringListConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> list) {
            return list == null ? "" : String.join("\n", list);
        }

        @Override
        public List<String> convertToEntityAttribute(String joined) {
            return joined == null || joined.trim().isEmpty() 
                ? new ArrayList<>() 
                : new ArrayList<>(java.util.Arrays.asList(joined.split("\n")));
        }
    }
}
