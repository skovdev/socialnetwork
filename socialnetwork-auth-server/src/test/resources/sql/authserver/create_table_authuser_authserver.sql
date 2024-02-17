CREATE TABLE
    sn_auth_user (
        id uuid NOT NULL,
        "password" varchar(255) NOT NULL,
        username varchar(255) NOT NULL,
        PRIMARY KEY (id)
);

CREATE TABLE
    sn_auth_role (
        id uuid NOT NULL,
        authority varchar(255) NOT NULL,
        user_id uuid NOT NULL,
        PRIMARY KEY (id)
);