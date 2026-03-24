# ============================================================
# Stage 1: Builder
# Uses full JDK to compile and package the application
# ============================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and dependency files FIRST (layer cache)
# These layers only rebuild when build.gradle / settings.gradle change
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer — won't re-download unless build.gradle changes)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q

# Copy source code and build the fat JAR
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test -q

# ============================================================
# Stage 2: Runtime
# Minimal JRE-only Alpine image — no compiler, no build tools
# ============================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: run as non-root user and upgrade OS packages to fix vulnerabilities (CVEs)
RUN apk upgrade --no-cache && \
    addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy only the fat JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8888

# JVM tuning flags:
#   -XX:+UseContainerSupport        → reads CPU/memory limits from container (not host)
#   -XX:MaxRAMPercentage=75.0       → use up to 75% of container memory for heap
#   -XX:+UseG1GC                    → reliable low-latency GC for server workloads
#   -Djava.security.egd=...         → faster SecureRandom startup
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
