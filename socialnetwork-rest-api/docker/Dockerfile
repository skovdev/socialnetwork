FROM openjdk:17

EXPOSE 8083

ADD ./wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-rest-api-1.0.jar socialnetwork-rest-api-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-rest-api-1.0.jar"]