#! /bin/bash

#mvn -f Docker_manager/ clean compile package shade:shade && mvn -f ServerApi/ clean compile package &&
docker-compose -f ./docker-compose.yml --project-directory ./ --env-file ./config_files/main.env up --build -V