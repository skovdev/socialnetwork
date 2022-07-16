FROM openjdk:14

EXPOSE 8761

ADD ./wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-service-discovery-1.0.jar socialnetwork-service-discovery-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-service-discovery-1.0.jar"]