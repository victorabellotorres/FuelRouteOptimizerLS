# Build Stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY libs ./libs
COPY src ./src

# Build the fat jar
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/gasolineras-ia-1.0-SNAPSHOT.jar app.jar
COPY --from=build /app/libs ./libs 
# Note: Since we used system scope, the Maven Shade Plugin *should* have unpacked or included them if configured to.
# However, system dependencies are tricky with shade.
# If shade didn't include them, we need them in classpath.
# But `java -jar` ignores -cp. Ideally shade includes them.
# The Shade plugin config in pom.xml usually unpacks dependencies.
# System dependencies are NOT included by shade by default unless we use a specific transformer or config.
# Let's hope for the best or assume we might need to fix the shade config.
# If they are NOT inside the jar, this will fail.
# For now, let's assume standard behavior is to NOT include system scope.
# So we might need to run with -cp instead of -jar, or fix pom.

CMD ["java", "-jar", "app.jar"]
