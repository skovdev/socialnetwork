FROM openjdk:20

EXPOSE 8080

ADD ../wait-for-it.sh /wait-for-it.sh

WORKDIR /app

COPY ../target/socialnetwork-auth-server-1.0.jar /app

ENTRYPOINT ["java", "-jar", "socialnetwork-auth-server-1.0.jar"]