# --- Build stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8090

# Healthcheck usando porta 8090
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:8090/actuator/health || exit 1

# Força o Spring Boot a rodar na porta 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--server.port=8090"]
