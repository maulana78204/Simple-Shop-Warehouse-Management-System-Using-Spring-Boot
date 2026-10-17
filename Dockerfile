# Multi-stage build for Warehouse Management System

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml dan download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src
COPY checkstyle.xml .

# Build application
RUN mvn clean package -DskipTests=true -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy JAR dari builder stage
COPY --from=builder /app/target/warehouse-management-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -cp app.jar com.warehouse.WarehouseManagementApplication || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
