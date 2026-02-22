FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY build/libs/wallet-sub-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]