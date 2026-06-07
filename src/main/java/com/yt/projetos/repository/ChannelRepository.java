package com.yt.projetos.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    List<Channel> findByUserId(UUID userId);

    @Query("SELECT COUNT(c) > 0 FROM Channel c WHERE c.id = :id AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT c FROM Channel c WHERE c.id = :id AND c.user.id = :userId")
    Optional<Channel> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
