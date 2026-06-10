-- Flyway migration to ensure channel_goals table exists
-- This table might have been skipped if added to V1 after initial migration

CREATE TABLE IF NOT EXISTS public.channel_goals (
    id              uuid            NOT NULL,
    completed       bool            NOT NULL    DEFAULT false,
    created_at      timestamp(3)    NOT NULL    DEFAULT now(),
    current_value   numeric(15,2)   NULL,
    deadline        timestamp(3)    NULL,
    description     varchar(255)    NULL,
    target_value    numeric(15,2)   NULL,
    title           varchar(255)    NOT NULL,
    channel_id      uuid            NOT NULL,
    CONSTRAINT channel_goals_pkey           PRIMARY KEY (id),
    CONSTRAINT fk_channel_goals_channel_id  FOREIGN KEY (channel_id) REFERENCES public.channels(id)
);

CREATE INDEX IF NOT EXISTS idx_channel_goals_channel_id
    ON channel_goals (channel_id);
