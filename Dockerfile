# ==============================================================================
# 1. BUILD STAGE (Compile & Package Spring Boot with Maven and Java 21)
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Build JAR package (skipping tests for faster build)
RUN mvn clean package -DskipTests

# ==============================================================================
# 2. RUNTIME STAGE (Lightweight JRE 21 Alpine image)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create non-root system user and group for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy compiled JAR artifact from builder
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Expose backend API port
EXPOSE 8888

# JVM Memory optimization
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
