FROM openjdk:20

EXPOSE 8084

ADD ../wait-for-it.sh /wait-for-it.sh

WORKDIR /app

COPY ../target/socialnetwork-user-service-1.0.jar /app

ENTRYPOINT ["java", "-jar", "socialnetwork-user-service-1.0.jar"]