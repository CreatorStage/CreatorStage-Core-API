package com.yt.projetos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yt.projetos.model.UploadedImage;

@Repository
public interface UploadedImageRepository extends JpaRepository<UploadedImage, UUID> {
}
