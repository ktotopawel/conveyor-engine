FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

COPY . .

RUN ./gradlew :conveyor-api:installDist -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/conveyor-api/build/install/conveyor-api /app

EXPOSE 8000

ENTRYPOINT ["/app/bin/conveyor-api"]
