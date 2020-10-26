FROM maven:3.6.3-openjdk-16
COPY ./src /build/src
COPY ./pom.xml /build/pom.xml
WORKDIR /build
RUN mvn package shade:shade
RUN rm target/original*.jar


FROM openjdk:16-jdk
#ubuntu:latest
#RUN apt update && apt install -y openjdk-14-jre
COPY --from=0 /build/target/*.jar /app/app.jar


CMD java -jar /app/app.jar

