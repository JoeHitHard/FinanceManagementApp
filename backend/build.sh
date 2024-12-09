#!/bin/sh

cd ./finance-core || exit
./gradlew clean build publishToMavenLocal
cd ..


cd ./finance-users || exit
./gradlew clean build
cd ..

cd ./finance-transaction || exit
./gradlew clean build
cd ..

docker-compose down --rmi all --volumes --remove-orphans finTrackr-user-service finTrackr-transaction-service
docker-compose build
docker-compose up -d
