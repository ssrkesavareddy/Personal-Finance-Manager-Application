FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/moneytracker-0.0.1-SNAPSHOT.jar moneytracker.jar
EXPOSE 9090
ENTRYPOINT["java", "jar","moneytracker.jar"]