#! /bin/bash

docker-compose -f ./docker-compose.yml --project-directory ./ --env-file ./config_files/main.env up --build -V
