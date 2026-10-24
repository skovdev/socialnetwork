-- Refresh tokens: persist a SHA-256 hash of an opaque random token instead of a plaintext UUID.
-- Existing sessions cannot be migrated (the plaintext was never hashed), so users sign in again.
DELETE FROM auth_refresh_tokens;
DROP INDEX IF EXISTS uq_auth_refresh_tokens_jti;
ALTER TABLE auth_refresh_tokens DROP COLUMN jti;
ALTER TABLE auth_refresh_tokens ADD COLUMN token_hash BYTEA NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_auth_refresh_tokens_token_hash ON auth_refresh_tokens (token_hash);

-- Support the scheduled cleanup of expired tokens
CREATE INDEX IF NOT EXISTS idx_auth_refresh_tokens_expires_at ON auth_refresh_tokens (expires_at);
CREATE INDEX IF NOT EXISTS idx_auth_email_tokens_expires_at ON auth_email_verification_tokens (expires_at);

-- Liker list (ORDER BY created_at DESC per post) and follower/following lists
CREATE INDEX IF NOT EXISTS idx_likes_post_created_at ON likes (post_id, created_at DESC);
DROP INDEX IF EXISTS idx_follows_followed_id;
CREATE INDEX IF NOT EXISTS idx_follows_followed_created_at ON follows (followed_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_follows_follower_created_at ON follows (follower_id, created_at DESC);

-- Redundant: UNIQUE constraints / low selectivity
DROP INDEX IF EXISTS idx_user_profiles_username;
DROP INDEX IF EXISTS idx_auth_users_status;
