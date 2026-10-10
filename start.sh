#!/bin/bash
python3 /app/secrets.py
[ ! -f .env ] || export $(grep -v '^#' .env | xargs)
java -jar application.jar
