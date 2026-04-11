FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

MAINTAINER kg

COPY target/medical-clinic-proxy-0.0.1-SNAPSHOT.jar medical-clinic-proxy-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","medical-clinic-proxy-0.0.1-SNAPSHOT.jar"]