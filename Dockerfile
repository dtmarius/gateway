FROM payara/server-web
COPY ./target/gateway.war ${DEPLOY_DIR}
