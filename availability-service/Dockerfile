# build stage, the jar is built inside the image so no local Maven or Java is needed
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

# run stage
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /build/target/availability-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
