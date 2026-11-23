# HAFTA 3 - ÖRNEK: Multi-stage build
# Build context proje kökü olmalı (parent pom gerekli):
#   docker build -f product-service/Dockerfile -t product-service .

# ---- 1. aşama: derleme (Maven + JDK, büyük image) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Önce sadece pom'ları kopyala: bağımlılıklar değişmedikçe bu katman cache'ten gelir.
COPY pom.xml .
COPY product-service/pom.xml product-service/
COPY order-service/pom.xml order-service/
RUN mvn -B -q -pl product-service -am dependency:go-offline

# Kaynak kod değişince sadece buradan sonrası yeniden çalışır.
COPY product-service/src product-service/src
RUN mvn -B -q -pl product-service -am package -DskipTests

# ---- 2. aşama: çalıştırma (sadece JRE, küçük image) ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Root olarak çalıştırma
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /workspace/product-service/target/product-service.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
