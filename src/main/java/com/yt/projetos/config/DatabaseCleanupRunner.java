package com.yt.projetos.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleanupRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando limpeza de dados legados vazios no banco de dados...");
        try {
            // Corrige campos vazios que quebram a desserialização de JSON pelo Jackson
            int videoIdeasTagsCount = jdbcTemplate.update(
                "UPDATE video_ideas SET tags = NULL WHERE tags::text = '' OR tags::text = '\"\"'"
            );
            int videoIdeasAltTitlesCount = jdbcTemplate.update(
                "UPDATE video_ideas SET alternative_titles = NULL WHERE alternative_titles::text = '' OR alternative_titles::text = '\"\"'"
            );
            int channelsCtaCount = jdbcTemplate.update(
                "UPDATE channels SET cta_templates = NULL WHERE cta_templates::text = '' OR cta_templates::text = '\"\"'"
            );
            int channelsDescCount = jdbcTemplate.update(
                "UPDATE channels SET description_blocks = NULL WHERE description_blocks::text = '' OR description_blocks::text = '\"\"'"
            );
            int channelsChecklistCount = jdbcTemplate.update(
                "UPDATE channels SET checklist_templates = NULL WHERE checklist_templates::text = '' OR checklist_templates::text = '\"\"'"
            );
            
            log.info("Limpeza concluída. Registros corrigidos: video_ideas.tags={}, video_ideas.alternative_titles={}, channels.cta_templates={}, channels.description_blocks={}, channels.checklist_templates={}",
                     videoIdeasTagsCount, videoIdeasAltTitlesCount, channelsCtaCount, channelsDescCount, channelsChecklistCount);
        } catch (Exception e) {
            log.error("Erro ao rodar limpeza de dados legados vazios no banco: ", e);
        }
    }
}
