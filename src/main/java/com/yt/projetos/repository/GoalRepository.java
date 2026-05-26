package com.yt.projetos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByChannelId(UUID channelId);
    List<Goal> findByChannelUserId(UUID userId);
}
