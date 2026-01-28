# Build Stage
FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files
COPY pom-railway.xml pom.xml
COPY libs ./libs
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Run Stage
FROM docker.io/library/eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/target/gasolineras-ia-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
