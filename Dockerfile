# -------- Build stage --------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# copy only wrapper first (better caching)
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# copy project
COPY . .

# use wrapper (THIS is the fix)
RUN ./gradlew clean build -x test

# -------- Run stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]