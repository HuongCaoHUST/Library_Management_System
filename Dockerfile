FROM eclipse-temurin:22-jdk
COPY target/Project-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]