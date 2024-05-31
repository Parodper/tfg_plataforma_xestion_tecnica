FROM maven:3.9.7-eclipse-temurin-17 AS BUILD_ENVIRONMENT

RUN mkdir -p /srv/pleste/pleste-client
COPY pleste-client/pom.xml /srv/pleste/pleste-client
COPY pom.xml /srv/pleste
COPY pleste-client/src /srv/pleste/pleste-client/src

WORKDIR /srv/pleste/pleste-client
RUN mvn package spring-boot:repackage

FROM eclipse-temurin:17-jre-ubi9-minimal

COPY --from=BUILD_ENVIRONMENT /srv/pleste/pleste-client/target/pleste-client-1.0-SNAPSHOT.jar /srv
WORKDIR /srv

ENTRYPOINT ["java", "-jar", "pleste-client-1.0-SNAPSHOT.jar"]