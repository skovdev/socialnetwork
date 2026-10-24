-- Optimistic locking: concurrent edits of the same post/comment no longer silently overwrite each other
ALTER TABLE posts ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
