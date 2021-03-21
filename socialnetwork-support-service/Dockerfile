FROM openjdk:14

EXPOSE 8089

ADD wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-support-service-1.0.jar socialnetwork-support-service-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-support-service-1.0.jar"]