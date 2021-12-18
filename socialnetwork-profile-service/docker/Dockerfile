FROM openjdk:14

EXPOSE 8081

ADD ../wait-for-it.sh /wait-for-it.sh

COPY ../target/socialnetwork-profile-service-1.0.jar socialnetwork-profile-service-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-profile-service-1.0.jar"]