CREATE TABLE
    sn_user (
        id uuid NOT NULL,
        address varchar(255) NULL,
        auth_user_id uuid NOT NULL,
        birth_day varchar(255) NULL,
        city varchar(255) NULL,
        country varchar(255) NULL,
        family_status varchar(255) NULL,
        first_name varchar(255) NULL,
        last_name varchar(255) NULL,
        phone varchar(255) NULL,
        PRIMARY KEY (id),
        UNIQUE (auth_user_id)
);