FROM maven:3.6.3-openjdk-16
WORKDIR /app
COPY ./pom.xml /app/pom.xml
COPY ./src /app/src



CMD mvn clean package && java -jar /app/target/*.jar

