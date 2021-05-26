FROM maven:3.6.3 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package spring-boot:repackage

FROM openjdk:11.0.11
WORKDIR /usr/local/lib
COPY --from=build /home/app/target/bolbolestan.jar .
COPY --from=build /home/app/src/main/resources ./src/main/resources
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/usr/local/lib/bolbolestan.jar" ]

