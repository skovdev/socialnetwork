CREATE USER sn_user_service_user WITH PASSWORD 'sn_user_service_password';
CREATE DATABASE sn_user_service_db;
GRANT ALL PRIVILEGES ON DATABASE sn_user_service_db to sn_user_service_user;

CREATE USER sn_profile_service_user WITH PASSWORD 'sn_profile_service_password';
CREATE DATABASE sn_profile_service_db;
GRANT ALL PRIVILEGES ON DATABASE sn_profile_service_db to sn_profile_service_user;

CREATE USER sn_group_service_user WITH PASSWORD 'sn_group_service_password';
CREATE DATABASE sn_group_service_db;
GRANT ALL PRIVILEGES ON DATABASE sn_group_service_db to sn_group_service_user