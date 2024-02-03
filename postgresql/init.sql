CREATE USER sn_auth_server_user WITH PASSWORD 'sn_auth_server_password';
CREATE DATABASE sn_auth_server_db;
GRANT ALL PRIVILEGES ON DATABASE sn_auth_server_db to sn_auth_server_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sn_auth_server_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sn_auth_server_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO sn_auth_server_user;
GRANT USAGE ON SCHEMA public TO sn_auth_server_user;
GRANT CREATE ON SCHEMA public TO sn_auth_server_user;

CREATE USER sn_user_service_user WITH PASSWORD 'sn_user_service_password';
CREATE DATABASE sn_user_service_db;
GRANT ALL PRIVILEGES ON DATABASE sn_user_service_db to sn_user_service_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sn_user_service_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sn_user_service_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO sn_user_service_user;
GRANT USAGE ON SCHEMA public TO sn_user_service_user;
GRANT CREATE ON SCHEMA public TO sn_user_service_user;

CREATE USER sn_profile_service_user WITH PASSWORD 'sn_profile_service_password';
CREATE DATABASE sn_profile_service_db;
GRANT ALL PRIVILEGES ON DATABASE sn_profile_service_db to sn_profile_service_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sn_profile_service_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sn_profile_service_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO sn_profile_service_user;
GRANT USAGE ON SCHEMA public TO sn_profile_service_user;
GRANT CREATE ON SCHEMA public TO sn_profile_service_user;

ALTER DATABASE sn_auth_server_db OWNER TO sn_auth_server_user;
ALTER DATABASE sn_user_service_db OWNER TO sn_user_service_user;
ALTER DATABASE sn_profile_service_db OWNER TO sn_profile_service_user;