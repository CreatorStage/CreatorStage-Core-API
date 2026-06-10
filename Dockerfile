# Build Stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy src and compile
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine

# Set security options & non-root user
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# Copy built artifact with correct ownership
COPY --chown=spring:spring --from=build /app/target/*.jar app.jar

# Run under the non-root user
USER spring:spring

EXPOSE 8080

# Keep JVM container-aware
ENTRYPOINT ["java", "-jar", "app.jar"]
