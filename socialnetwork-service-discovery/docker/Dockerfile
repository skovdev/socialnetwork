FROM openjdk:20

EXPOSE 8761

ADD ../wait-for-it.sh /wait-for-it.sh

WORKDIR /app

COPY ../target/socialnetwork-service-discovery-1.0.jar /app

ENTRYPOINT ["java", "-jar", "socialnetwork-service-discovery-1.0.jar"]