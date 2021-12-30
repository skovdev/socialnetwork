FROM postgres:12

EXPOSE 5432

COPY ./init.sql /docker-entrypoint-initdb.d/init.sql

CMD ["docker-entrypoint.sh", "postgres"]