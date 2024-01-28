CREATE TABLE
    sn_profile (
        id uuid NOT NULL,
        avatar oid NULL,
        is_active bool NULL,
        user_id uuid NOT NULL,
        PRIMARY KEY (id),
        UNIQUE (user_id)
);