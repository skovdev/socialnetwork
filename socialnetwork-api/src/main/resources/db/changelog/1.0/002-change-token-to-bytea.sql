ALTER TABLE auth_email_verification_tokens
ALTER COLUMN token TYPE BYTEA
USING token::bytea