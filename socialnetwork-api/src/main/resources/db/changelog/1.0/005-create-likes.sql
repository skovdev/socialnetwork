CREATE TABLE IF NOT EXISTS likes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    author_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_likes_post
        FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_author
        FOREIGN KEY (author_id) REFERENCES auth_users (id) ON DELETE CASCADE,
    CONSTRAINT uq_likes_post_author
        UNIQUE (post_id, author_id)
);

CREATE INDEX IF NOT EXISTS idx_likes_post_id ON likes (post_id);
CREATE INDEX IF NOT EXISTS idx_likes_author_id ON likes (author_id);
