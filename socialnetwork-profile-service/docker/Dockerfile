FROM openjdk:20

EXPOSE 8081

ADD ../wait-for-it.sh /wait-for-it.sh

WORKDIR /app

COPY ../target/socialnetwork-profile-service-1.0.jar /app

ENTRYPOINT ["java", "-jar", "socialnetwork-profile-service-1.0.jar"]