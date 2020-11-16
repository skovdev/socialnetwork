FROM openjdk:14

EXPOSE 8762

ADD wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-api-gateway-1.0.jar socialnetwork-api-gateway-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-api-gateway-1.0.jar"]