CREATE TABLE IF NOT EXISTS follows (
    id UUID PRIMARY KEY,
    follower_id UUID NOT NULL,
    followed_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_follows_follower
        FOREIGN KEY (follower_id) REFERENCES auth_users (id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_followed
        FOREIGN KEY (followed_id) REFERENCES auth_users (id) ON DELETE CASCADE,
    CONSTRAINT uq_follows_follower_followed
        UNIQUE (follower_id, followed_id),
    CONSTRAINT chk_follows_no_self_follow
        CHECK (follower_id <> followed_id)
);

CREATE INDEX IF NOT EXISTS idx_follows_follower_id ON follows (follower_id);
CREATE INDEX IF NOT EXISTS idx_follows_followed_id ON follows (followed_id);
