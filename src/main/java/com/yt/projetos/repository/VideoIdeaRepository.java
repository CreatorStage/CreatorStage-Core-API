package com.yt.projetos.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.VideoIdea;

@Repository
public interface VideoIdeaRepository extends JpaRepository<VideoIdea, UUID> {
    List<VideoIdea> findByChannelId(UUID channelId);
    List<VideoIdea> findByChannelUserId(UUID userId);

    @Query("SELECT COUNT(v) > 0 FROM VideoIdea v WHERE v.id = :id AND v.channel.user.id = :userId")
    boolean existsByIdAndChannelUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT v FROM VideoIdea v WHERE v.id = :id AND v.channel.user.id = :userId")
    Optional<VideoIdea> findByIdAndChannelUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
