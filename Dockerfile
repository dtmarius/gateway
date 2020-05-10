FROM airhacks/glassfish
COPY ./target/brickfolio.war ${DEPLOYMENT_DIR}
