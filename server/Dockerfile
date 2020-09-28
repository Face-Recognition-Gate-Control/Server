FROM ubuntu:latest

RUN apt update && apt install -y curl
RUN curl -sSL https://get.docker.com/ | sh
RUN apt update && apt install -y openjdk-14-jre


COPY ./target/server-1.0-SNAPSHOT.jar /app/server-1.0-SNAPSHOT.jar


CMD java -jar /app/server-1.0-SNAPSHOT.jar

