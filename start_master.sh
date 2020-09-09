#! /bin/bash

#mvn -f Docker_manager/ clean compile package shade:shade && mvn -f ServerApi/ clean compile package &&
docker-compose -f ./DockerFiles/master.docker-compose.yml --project-directory ./ --env-file ./ConfigFiles/main.env up --build -V