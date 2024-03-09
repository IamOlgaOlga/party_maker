FROM openjdk:21

# Copy the built jar file and migration files into the container.
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY db/migration /db/migration

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/app.jar"]