CREATE UNIQUE INDEX IF NOT EXISTS uq_auth_refresh_tokens_jti ON auth_refresh_tokens (jti);
CREATE INDEX IF NOT EXISTS idx_auth_email_tokens_token ON auth_email_verification_tokens (token);

CREATE INDEX IF NOT EXISTS idx_posts_author_created_at ON posts (author_id, created_at DESC);
DROP INDEX IF EXISTS idx_posts_author_id;

CREATE INDEX IF NOT EXISTS idx_comments_post_toplevel_created_at
    ON comments (post_id, created_at) WHERE parent_comment_id IS NULL;

DROP INDEX IF EXISTS idx_likes_post_id;
DROP INDEX IF EXISTS idx_follows_follower_id;
