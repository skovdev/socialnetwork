CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE auth_users (
    id UUID PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    status TEXT NOT NULL CHECK (
        status IN ('PENDING_VERIFICATION', 'ACTIVE', 'DISABLED')
    ),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE auth_user_roles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    authority TEXT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_auth_user_roles_user
        FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE
);

CREATE TABLE auth_email_verification_tokens (
    id UUID PRIMARY KEY,
    token TEXT NOT NULL,
    user_id UUID NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    CONSTRAINT fk_auth_email_verification_tokens_user
        FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE
);

CREATE TABLE auth_refresh_tokens (
    id UUID PRIMARY KEY,
    jti UUID NOT NULL,
    user_id UUID NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL
    CONSTRAINT fk_auth_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_auth_users_status ON auth_users (status);
CREATE INDEX IF NOT EXISTS idx_auth_refresh_tokens_user ON auth_refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_auth_email_tokens_user ON auth_email_verification_tokens (user_id);

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE
    username TEXT UNIQUE NOT NULL,
    display_name TEXT NOT NULL,
    first_name TEXT,
    last_name TEXT,
    bio TEXT,
    avatar_url TEXT,
    birth_date DATE,
    phone TEXT,
    country TEXT,
    city TEXT,
    address TEXT,
    family_status TEXT,
    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_username ON user_profiles (username);