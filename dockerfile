FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Install required packages
RUN apt-get update && apt-get install -y findutils

# Cache gradle dependencies
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew

COPY src src
RUN ./gradlew build -x test

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
