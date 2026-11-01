FROM ghcr.io/graalvm/native-image-community:25 AS build
RUN microdnf install -y findutils && microdnf clean all
WORKDIR /workspace
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle gradle
COPY src src
RUN chmod +x gradlew && ./gradlew nativeCompile --no-daemon -x test

FROM debian:bookworm-slim
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/build/native/nativeCompile/Reminder /app/reminder
EXPOSE 8000
ENTRYPOINT ["/app/reminder", "-Xmx256m"]
