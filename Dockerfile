FROM postgres
ENV POSTGRES_DB pleste
COPY src/main/resources/setup.sql /docker-entrypoint-initdb.d/