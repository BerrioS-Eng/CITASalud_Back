FROM eclipse-temurin:20-jdk AS builder

WORKDIR /app

COPY . .

# Copiar el Maven Wrapper
COPY mvnw .
COPY .mvn .mvn

# Dar permisos de ejecución
RUN chmod +x mvnw


RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:20-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]