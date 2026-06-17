FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

COPY . .

RUN ./gradlew :conveyor-worker:installDist -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/conveyor-worker/build/install/conveyor-worker /app

ENTRYPOINT ["/app/bin/conveyor-worker"]
