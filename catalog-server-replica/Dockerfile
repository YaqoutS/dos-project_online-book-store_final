FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/catalog-server-replica.jar .
COPY catalog.db .

CMD ["java", "-jar", "catalog-server-replica.jar"]
