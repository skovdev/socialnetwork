FROM openjdk:20

EXPOSE 8888

WORKDIR /app

COPY ../target/socialnetwork-config-server-1.0.jar /app

ENTRYPOINT ["java", "-jar", "socialnetwork-config-server-1.0.jar"]