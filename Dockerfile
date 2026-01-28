# Run Stage
FROM docker.io/library/eclipse-temurin:17-jre-alpine

WORKDIR /app
# Copy locally built jar
COPY target/gasolineras-ia-1.0-SNAPSHOT.jar app.jar
COPY libs ./libs 

CMD ["java", "-jar", "app.jar"]

