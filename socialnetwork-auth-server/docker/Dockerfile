FROM openjdk:19

EXPOSE 8080

ADD ./wait-for-it.sh /wait-for-it.sh

COPY ./target/socialnetwork-auth-server-1.0.jar socialnetwork-auth-server-1.0.jar

ENTRYPOINT ["java", "-jar", "socialnetwork-auth-server-1.0.jar"]