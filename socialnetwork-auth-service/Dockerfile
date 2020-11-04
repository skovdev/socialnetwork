FROM openjdk:14

EXPOSE 8080

ADD wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-auth-service-1.0.jar socialnetwork-auth-service-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-auth-service-1.0.jar"]