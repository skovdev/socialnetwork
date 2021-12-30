FROM openjdk:14

EXPOSE 8084

ADD ./wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-user-service-1.0.jar socialnetwork-user-service-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-user-service-1.0.jar"]