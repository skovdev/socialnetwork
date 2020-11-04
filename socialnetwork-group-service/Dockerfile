FROM openjdk:14

EXPOSE 8085

ADD wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-group-service-1.0.jar socialnetwork-group-service-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-group-service-1.0.jar"]