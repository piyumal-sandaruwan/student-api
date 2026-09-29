# ===================================================================
# Dockerfile for student-api
# Runtime: Java 25 Eclipse Temurin
# ===================================================================

FROM eclipse-temurin:25-jdk-noble

# Set working directory
WORKDIR /app

# Non-root user for container security best practice
RUN groupadd -r spring && useradd -r -g spring spring

# Copy pre-built executable JAR from Maven target directory
ARG JAR_FILE=target/student-api-*.jar
COPY ${JAR_FILE} app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app
USER spring:spring

# Expose Spring Boot default application port
EXPOSE 8080

# Configure JVM flags and launch application
ENTRYPOINT ["java", "-XX:+EnableDynamicAgentLoading", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
