FROM maven:3.9.7-eclipse-temurin-17 AS BUILD_ENVIRONMENT

RUN mkdir -p /srv/pleste/pleste-server
COPY pleste-server/pom.xml /srv/pleste/pleste-server
COPY pom.xml /srv/pleste
COPY pleste-server/src /srv/pleste/pleste-server/src

WORKDIR /srv/pleste/pleste-server
RUN mvn package spring-boot:repackage

FROM eclipse-temurin:17-jre-ubi9-minimal

COPY --from=BUILD_ENVIRONMENT /srv/pleste/pleste-server/target/pleste-server-1.0-SNAPSHOT.jar /srv
WORKDIR /srv

ENTRYPOINT ["java", "-jar", "pleste-server-1.0-SNAPSHOT.jar"]