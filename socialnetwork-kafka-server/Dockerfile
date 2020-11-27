FROM openjdk:14

EXPOSE 8087

COPY ./target/socialnetwork-kafka-server-1.0.jar socialnetwork-kafka-server-1.0.jar

ENTRYPOINT["java", "-jar", "socialnetwork-kafka-server-1.0.jar"]