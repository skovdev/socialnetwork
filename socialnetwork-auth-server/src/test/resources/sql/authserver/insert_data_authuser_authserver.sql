insert into
    sn_auth_user (
    id,
    username,
    "password")
values (
           '52721169-01f7-41ed-a8f3-683abf47765d',
           'testUsername',
           '$2a$10$zkMhDs9dYl8ZDGKY/lPwcOtQJC1r3RBKhJ.R.MiQKElfg0UqYr8uy'
       );

insert into
    sn_auth_role (
        id,
        authority,
        user_id
)
values (
        'e4fe1781-7d45-462d-8751-c754309e804a',
        'USER',
        '52721169-01f7-41ed-a8f3-683abf47765d'
);
