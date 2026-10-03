-- Seeds 3 ready-to-use, already-ACTIVE test accounts for local manual UI testing (e.g. the
-- Followers feature) without going through POST /auth/register + AWS SES email verification.
--
-- This is a standalone dev utility, NOT a Liquibase migration — it is never run automatically
-- and never ships to any real environment. Run it by hand against your local Postgres:
--
--   psql -h localhost -U socialnetwork_user -d socialnetwork_db -f scripts/seed-local-test-users.sql
--
-- All three accounts share the password: Secret1234
-- (the bcrypt hash below was generated with the app's own BCryptPasswordEncoder, strength 10 —
-- see SecurityConfig.passwordEncoder())
--
-- Safe to re-run: each user is only inserted if its email doesn't already exist.

DO $$
DECLARE
    seeded_password_hash CONSTANT TEXT := '$2a$10$O4EQ/Yh1kjkQkLatpWzJQO8aGBtW5ABPpAJHKr4WOv8P8zUobyRde';
    new_user_id UUID;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM auth_users WHERE email = 'alice@example.com') THEN
        new_user_id := uuid_generate_v4();
        INSERT INTO auth_users (id, email, password_hash, status, created_at)
            VALUES (new_user_id, 'alice@example.com', seeded_password_hash, 'ACTIVE', now());
        INSERT INTO auth_user_roles (id, user_id, authority)
            VALUES (uuid_generate_v4(), new_user_id, 'ROLE_USER');
        INSERT INTO user_profiles (id, user_id, username, display_name, first_name, last_name)
            VALUES (uuid_generate_v4(), new_user_id, 'alice', 'Alice Smith', 'Alice', 'Smith');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM auth_users WHERE email = 'bob@example.com') THEN
        new_user_id := uuid_generate_v4();
        INSERT INTO auth_users (id, email, password_hash, status, created_at)
            VALUES (new_user_id, 'bob@example.com', seeded_password_hash, 'ACTIVE', now());
        INSERT INTO auth_user_roles (id, user_id, authority)
            VALUES (uuid_generate_v4(), new_user_id, 'ROLE_USER');
        INSERT INTO user_profiles (id, user_id, username, display_name, first_name, last_name)
            VALUES (uuid_generate_v4(), new_user_id, 'bob', 'Bob Jones', 'Bob', 'Jones');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM auth_users WHERE email = 'charlie@example.com') THEN
        new_user_id := uuid_generate_v4();
        INSERT INTO auth_users (id, email, password_hash, status, created_at)
            VALUES (new_user_id, 'charlie@example.com', seeded_password_hash, 'ACTIVE', now());
        INSERT INTO auth_user_roles (id, user_id, authority)
            VALUES (uuid_generate_v4(), new_user_id, 'ROLE_USER');
        INSERT INTO user_profiles (id, user_id, username, display_name, first_name, last_name)
            VALUES (uuid_generate_v4(), new_user_id, 'charlie', 'Charlie Third', 'Charlie', 'Third');
    END IF;
END $$;
