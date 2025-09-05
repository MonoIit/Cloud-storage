FROM maven:3.9.5 AS build
WORKDIR /code
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy
COPY --from=build /code/target/cloud-service-0.0.1-SNAPSHOT.jar /usr/bin/app-1.0.0.jar
CMD ["java", "-jar", "/usr/bin/app-1.0.0.jar"]