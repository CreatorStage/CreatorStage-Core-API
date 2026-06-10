ALTER TABLE IF EXISTS channels ALTER COLUMN checklist_templates TYPE jsonb USING checklist_templates::jsonb;
ALTER TABLE IF EXISTS channels ALTER COLUMN cta_templates TYPE jsonb USING cta_templates::jsonb;
ALTER TABLE IF EXISTS channels ALTER COLUMN description_blocks TYPE jsonb USING description_blocks::jsonb;
