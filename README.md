# Build
mvn clean package && docker build -t com.dtmarius/brickfolio .

# RUN

docker rm -f brickfolio || true && docker run -d -p 8080:8080 -p 4848:4848 --name brickfolio com.dtmarius/brickfolio 