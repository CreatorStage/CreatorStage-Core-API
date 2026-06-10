-- flyway:disableTransaction
-- ============================================================
-- SCHEMA COMPLETO + OTIMIZAÇÕES
-- PostgreSQL — executar com autocommit ATIVO
--
-- psql:    \set AUTOCOMMIT on
-- DBeaver: ativar autocommit no toolbar antes de executar
-- Flyway:  adicionar "-- flyway:disableTransaction" no topo
-- ============================================================


-- ============================================================
-- FASE 1: CRIAÇÃO DAS TABELAS
-- ============================================================

CREATE TABLE IF NOT EXISTS public.uploaded_images (
    id              uuid            NOT NULL,
    content_type    varchar(255)    NOT NULL,
    created_at      timestamp(3)    NOT NULL    DEFAULT now(),
    "data"          oid             NOT NULL,
    filename        varchar(255)    NOT NULL,
    CONSTRAINT uploaded_images_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.users (
    id          uuid            NOT NULL,
    created_at  timestamp(3)    NOT NULL    DEFAULT now(),
    email       varchar(255)    NOT NULL,
    "name"      varchar(255)    NOT NULL,
    "password"  varchar(255)    NOT NULL,
    CONSTRAINT users_pkey           PRIMARY KEY (id),
    CONSTRAINT users_email_unique   UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.user_settings (
    user_id                 uuid            NOT NULL,
    email_notifications     bool            NOT NULL,
    preferred_language      varchar(255)    NULL,
    profile_bio             varchar(255)    NULL,
    theme                   varchar(255)    NULL,
    CONSTRAINT user_settings_pkey       PRIMARY KEY (user_id),
    CONSTRAINT fk_user_settings_user_id FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE IF NOT EXISTS public.channels (
    id                      uuid            NOT NULL,
    checklist_templates     jsonb           NULL,       -- era text
    created_at              timestamp(3)    NOT NULL    DEFAULT now(),
    cta_templates           jsonb           NULL,       -- era text
    deleted_at              timestamp(3)    NULL,
    description_blocks      jsonb           NULL,       -- era text
    "name"                  varchar(255)    NOT NULL,
    niche                   varchar(255)    NOT NULL,
    user_id                 uuid            NOT NULL,
    CONSTRAINT channels_pkey        PRIMARY KEY (id),
    CONSTRAINT fk_channels_user_id  FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE IF NOT EXISTS public.channel_goals (
    id              uuid            NOT NULL,
    completed       bool            NOT NULL    DEFAULT false,
    created_at      timestamp(3)    NOT NULL    DEFAULT now(),
    current_value   numeric(15,2)   NULL,       -- era float8
    deadline        timestamp(3)    NULL,
    description     varchar(255)    NULL,
    target_value    numeric(15,2)   NULL,       -- era float8
    title           varchar(255)    NOT NULL,
    channel_id      uuid            NOT NULL,
    CONSTRAINT channel_goals_pkey           PRIMARY KEY (id),
    CONSTRAINT fk_channel_goals_channel_id  FOREIGN KEY (channel_id) REFERENCES public.channels(id)
);

CREATE TABLE IF NOT EXISTS public.channel_reference_links (
    id              uuid            NOT NULL,
    created_at      timestamp(3)    NOT NULL    DEFAULT now(),
    note            text            NULL,
    title           varchar(255)    NOT NULL,
    url             text            NOT NULL,
    channel_id      uuid            NOT NULL,
    thumbnail_url   text            NULL,
    "type"          varchar(255)    NULL,
    CONSTRAINT channel_reference_links_pkey             PRIMARY KEY (id),
    CONSTRAINT channel_reference_links_type_check       CHECK (((type)::text = ANY ((ARRAY['LINK'::character varying, 'THUMBNAIL'::character varying, 'TITLE'::character varying])::text[]))),
    CONSTRAINT fk_channel_reference_links_channel_id    FOREIGN KEY (channel_id) REFERENCES public.channels(id)
);

CREATE TABLE IF NOT EXISTS public.suggested_videos (
    id                      uuid            NOT NULL,
    created_at              timestamp(3)    NOT NULL    DEFAULT now(),
    source_channel_name     varchar(255)    NULL,
    source_channel_url      text            NULL,
    thumbnail_url           text            NULL,
    title                   varchar(255)    NOT NULL,
    url                     text            NOT NULL,
    views_count             bigint          NULL,       -- era varchar(255) "views"
    channel_id              uuid            NOT NULL,
    CONSTRAINT suggested_videos_pkey            PRIMARY KEY (id),
    CONSTRAINT fk_suggested_videos_channel_id   FOREIGN KEY (channel_id) REFERENCES public.channels(id)
);

CREATE TABLE IF NOT EXISTS public.video_ideas (
    id                  uuid            NOT NULL,
    checklist_state     text            NULL,
    created_at          timestamp(3)    NOT NULL    DEFAULT now(),
    deadline            timestamp(3)    NULL,
    deleted_at          timestamp(3)    NULL,
    description         text            NULL,
    evergreen           bool            NULL,
    main_title          varchar(255)    NOT NULL,
    published_url       varchar(255)    NULL,
    -- campos de sponsor removidos → tabela video_idea_sponsorships
    status              varchar(255)    NOT NULL,
    trend               bool            NULL,
    updated_at          timestamp(3)    NULL,
    alternative_titles  jsonb           NULL,       -- era tabela video_idea_alternative_titles
    tags                jsonb           NULL,       -- era tabela video_idea_tags
    channel_id          uuid            NOT NULL,
    CONSTRAINT video_ideas_pkey         PRIMARY KEY (id),
    CONSTRAINT video_ideas_status_check CHECK (((status)::text = ANY ((ARRAY['IDEA'::character varying, 'RESEARCHING'::character varying, 'SCRIPTING'::character varying, 'READY_TO_RECORD'::character varying, 'RECORDED'::character varying, 'EDITING'::character varying, 'SCHEDULED'::character varying, 'PUBLISHED'::character varying, 'ARCHIVED'::character varying])::text[]))),
    CONSTRAINT fk_video_ideas_channel_id FOREIGN KEY (channel_id) REFERENCES public.channels(id)
);

-- Patrocínios extraídos de video_ideas → tabela própria
-- Permite múltiplos patrocinadores por vídeo no futuro
CREATE TABLE IF NOT EXISTS public.video_idea_sponsorships (
    id              uuid            NOT NULL,
    video_idea_id   uuid            NOT NULL,
    brand           varchar(255)    NULL,
    deadline        timestamp(3)    NULL,
    payment_status  varchar(255)    NULL,
    tracking_url    text            NULL,
    value           numeric(15,2)   NULL,           -- era float8
    sponsored       bool            NOT NULL        DEFAULT false,
    created_at      timestamp(3)    NOT NULL        DEFAULT now(),
    CONSTRAINT video_idea_sponsorships_pkey             PRIMARY KEY (id),
    CONSTRAINT fk_sponsorships_video_idea_id            FOREIGN KEY (video_idea_id) REFERENCES public.video_ideas(id)
);

CREATE TABLE IF NOT EXISTS public.video_references (
    id              uuid            NOT NULL,
    created_at      timestamp(3)    NOT NULL    DEFAULT now(),
    "label"         varchar(255)    NOT NULL,
    "type"          varchar(255)    NOT NULL,
    url             text            NULL,
    image_id        uuid            NULL,
    video_idea_id   uuid            NOT NULL,
    CONSTRAINT video_references_pkey                PRIMARY KEY (id),
    CONSTRAINT video_references_image_id_unique     UNIQUE (image_id),
    CONSTRAINT fk_video_references_video_idea_id    FOREIGN KEY (video_idea_id) REFERENCES public.video_ideas(id),
    CONSTRAINT fk_video_references_image_id         FOREIGN KEY (image_id) REFERENCES public.uploaded_images(id)
);

-- video_scripts removida — unificada em script_versions com is_current
-- script_versions é a única fonte de verdade para scripts
CREATE TABLE IF NOT EXISTS public.script_versions (
    id                          uuid            NOT NULL,
    "content"                   text            NULL,
    content_type                varchar(255)    NOT NULL,
    created_at                  timestamp(3)    NOT NULL    DEFAULT now(),
    deleted_at                  timestamp(3)    NULL,
    estimated_duration_seconds  int4            NOT NULL,
    "label"                     varchar(255)    NOT NULL,
    updated_at                  timestamp(3)    NULL,
    word_count                  int4            NOT NULL,
    is_current                  bool            NOT NULL    DEFAULT false,  -- substitui video_scripts
    video_idea_id               uuid            NOT NULL,
    CONSTRAINT script_versions_pkey             PRIMARY KEY (id),
    CONSTRAINT fk_script_versions_video_idea_id FOREIGN KEY (video_idea_id) REFERENCES public.video_ideas(id)
);

CREATE TABLE IF NOT EXISTS public.notes (
    id              uuid            NOT NULL,
    "content"       text            NULL,
    created_at      timestamp(3)    NOT NULL    DEFAULT now(),
    updated_at      timestamp(3)    NULL,
    video_idea_id   uuid            NOT NULL,
    CONSTRAINT notes_pkey               PRIMARY KEY (id),
    CONSTRAINT fk_notes_video_idea_id   FOREIGN KEY (video_idea_id) REFERENCES public.video_ideas(id)
);


-- ============================================================
-- FASE 2: ÍNDICES EM FOREIGN KEYS
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_channels_user_id
    ON channels (user_id);

CREATE INDEX IF NOT EXISTS idx_suggested_videos_channel_id
    ON suggested_videos (channel_id);

CREATE INDEX IF NOT EXISTS idx_video_ideas_channel_id
    ON video_ideas (channel_id);

CREATE INDEX IF NOT EXISTS idx_video_references_video_idea_id
    ON video_references (video_idea_id);

-- image_id já tem UNIQUE constraint — índice implícito, não duplicar

CREATE INDEX IF NOT EXISTS idx_channel_goals_channel_id
    ON channel_goals (channel_id);

CREATE INDEX IF NOT EXISTS idx_channel_reference_links_channel_id
    ON channel_reference_links (channel_id);

CREATE INDEX IF NOT EXISTS idx_notes_video_idea_id
    ON notes (video_idea_id);

CREATE INDEX IF NOT EXISTS idx_script_versions_video_idea_id
    ON script_versions (video_idea_id);

CREATE INDEX IF NOT EXISTS idx_sponsorships_video_idea_id
    ON video_idea_sponsorships (video_idea_id);


-- ============================================================
-- FASE 3: ÍNDICES PARCIAIS EM SOFT-DELETES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_channels_active
    ON channels (user_id, name)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_video_ideas_active
    ON video_ideas (channel_id, status)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_script_versions_active
    ON script_versions (video_idea_id)
    WHERE deleted_at IS NULL;


-- ============================================================
-- FASE 4: ÍNDICE COMPOSTO PARA QUERIES DE KANBAN
-- Cobre: WHERE channel_id = ? AND status = ? ORDER BY created_at DESC
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_video_ideas_channel_status_created
    ON video_ideas (channel_id, status, created_at DESC)
    WHERE deleted_at IS NULL;


-- ============================================================
-- FASE 5: ÍNDICE ÚNICO — garante no máximo 1 script atual por vídeo
-- ============================================================

CREATE UNIQUE INDEX IF NOT EXISTS idx_script_versions_current
    ON script_versions (video_idea_id)
    WHERE is_current = true;


-- ============================================================
-- FASE 6: ÍNDICES GIN — busca dentro de jsonb (tags, títulos alternativos)
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_video_ideas_tags_gin
    ON video_ideas USING gin (tags);

CREATE INDEX IF NOT EXISTS idx_video_ideas_alt_titles_gin
    ON video_ideas USING gin (alternative_titles);


-- ============================================================
-- VALIDAÇÃO — rodar após alguns dias em produção
-- ============================================================

-- Uso dos índices criados
-- SELECT relname, indexrelname, idx_scan, idx_tup_read, idx_tup_fetch
-- FROM pg_stat_user_indexes
-- WHERE relname IN (
--     'video_ideas', 'channels', 'suggested_videos',
--     'video_references', 'notes', 'script_versions',
--     'channel_goals', 'channel_reference_links',
--     'video_idea_sponsorships'
-- )
-- ORDER BY idx_scan DESC;

-- Seq Scans restantes (idx_scan = 0 após 30 dias → candidato a remoção)
-- SELECT relname, seq_scan, seq_tup_read, idx_scan
-- FROM pg_stat_user_tables
-- WHERE relname IN (
--     'video_ideas', 'channels', 'suggested_videos',
--     'notes', 'script_versions'
-- )
-- ORDER BY seq_scan DESC;

-- Confirmar índice do kanban sendo usado
-- EXPLAIN (ANALYZE, BUFFERS)
-- SELECT id, main_title, status, created_at
-- FROM video_ideas
-- WHERE channel_id = '00000000-0000-0000-0000-000000000000'
--   AND status     = 'SCRIPTING'
--   AND deleted_at IS NULL
-- ORDER BY created_at DESC;

-- Confirmar que cada vídeo tem no máximo 1 script atual
-- SELECT video_idea_id, COUNT(*) AS current_count
-- FROM script_versions
-- WHERE is_current = true
-- GROUP BY video_idea_id
-- HAVING COUNT(*) > 1;   -- deve retornar 0 linhas
