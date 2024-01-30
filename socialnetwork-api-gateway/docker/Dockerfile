FROM openjdk:20

EXPOSE 8762

ADD ../wait-for-it.sh /wait-for-it.sh

WORKDIR /app

COPY ../target/socialnetwork-api-gateway-1.0.jar /app

ENTRYPOINT ["java", "-jar", "socialnetwork-api-gateway-1.0.jar"]