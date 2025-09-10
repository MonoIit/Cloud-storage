FROM gradle:8.9-jdk21 AS build

RUN mkdir /code
COPY . /code

WORKDIR /code

RUN ./gradlew build



FROM eclipse-temurin:21-jre-jammy
COPY --from=build /code/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]