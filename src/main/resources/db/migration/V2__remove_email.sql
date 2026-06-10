-- 1. Adicionar coluna username na tabela users
ALTER TABLE users ADD COLUMN username varchar(255);

-- 2. Migrar dados: usar parte do email antes do @ como username
UPDATE users SET username = split_part(email, '@', 1) || '_' || substr(id::text, 1, 8);

-- 3. Tornar username NOT NULL e UNIQUE
ALTER TABLE users ALTER COLUMN username SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT users_username_unique UNIQUE (username);

-- 4. Remover constraint e coluna email
ALTER TABLE users DROP CONSTRAINT users_email_unique;
ALTER TABLE users DROP COLUMN email;

-- 5. Remover coluna email_notifications de user_settings
ALTER TABLE user_settings DROP COLUMN email_notifications;
