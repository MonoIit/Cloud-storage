FROM gradle:8.9-jdk21 AS build

WORKDIR /code

COPY build.gradle settings.gradle gradlew* ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || return 0

COPY . .

RUN ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:21-jre-jammy
COPY --from=build /code/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]