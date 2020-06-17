#!/bin/sh
mvn clean package && docker build -t com.dtmarius/gateway .
docker rm -f gateway || true && docker run -d -p 8080:8080 -p 4848:4848 --name gateway com.dtmarius/gateway 
