# -------- Build stage --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy ONLY files needed to resolve dependencies first (best caching)
COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew --version

# Now copy source
COPY src src

# Build
RUN ./gradlew clean build -x test

# -------- Run stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]