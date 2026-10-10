#!/bin/bash

cleanup () {
  rm -rf build
}

rm -rf build
mkdir build

while getopts e: flag
do
    case "${flag}" in
        e) env=${OPTARG};;
        *)
    esac
done

if [ "$env" = "staging" ]; then
  cp src/main/resources/procfile/Procfile-staging build/Procfile
  cp -r src/main/resources/.ebextensions build/
elif [ "$env" = "prod" ]; then
  cp src/main/resources/procfile/Procfile-prod build/Procfile
  cp -r src/main/resources/.ebextensions build/
elif [ "$env" = "clean" ]; then
  cleanup
else
    echo "Error! Please specify env name [staging/prod] with -e flag"
    cleanup
    exit
fi

mvn clean package -DskipTests

date=$(date '+%Y-%m-%d_%H:%M')
targetFile="build/application.jar"
cp -a target/samaaj-seva-0.0.1-SNAPSHOT.jar "$targetFile"
cd build || exit
zip -r "samaaj-seva-${date}-${env}.zip" .

echo "********************** Done **********************"
