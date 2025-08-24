# Dockerfile para Automo Backend - Java 21
# Baseado no afrikancoders-backend

# Build stage com Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
ARG ENV
WORKDIR /app
COPY . .

# Compilar com Java 21
RUN mvn clean package -DskipTests

# Runtime stage com Java 21
FROM eclipse-temurin:21-jdk
ARG ENV
ENV ENV=$ENV
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"] 