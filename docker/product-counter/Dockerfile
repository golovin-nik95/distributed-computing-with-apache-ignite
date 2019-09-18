FROM adoptopenjdk/openjdk11:alpine-slim
COPY ["build/libs/distributed-computing-with-apache-ignite-1.0-SNAPSHOT.jar", "app.jar"]
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /app.jar" ]
