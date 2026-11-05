# ---------- Etapa 1: build (JDK 25 + Maven Wrapper) ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copia primeiro só o necessário para baixar dependências (aproveita o cache de camadas).
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src/ src/
# Os testes rodam no CI/local (./mvnw test); aqui só empacotamos.
RUN ./mvnw -B -q clean package -DskipTests && cp target/*.jar app.jar

# ---------- Etapa 2: runtime (apenas JRE, imagem menor) ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Usuário sem privilégios
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /app/app.jar app.jar

# O Render informa a porta na variável PORT (application.properties usa ${PORT:8080}).
ENV PORT=8080
EXPOSE 8080

# Limita a heap a 75% da memória do container (plano gratuito do Render: 512 MB).
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+UseSerialGC", "-jar", "app.jar"]
