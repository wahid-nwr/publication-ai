FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/ai-document-intelligence-1.0-SNAPSHOT.jar app.jar
COPY target/lib ./lib
COPY truststore/keycloak.crt /tmp/keycloak.crt

# Import into JVM default truststore
RUN keytool -import \
    -alias keycloak-jwk \
    -file /tmp/keycloak.crt \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass $STORE_PASS \
    -noprompt

COPY scripts/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
RUN chmod 644 app.jar

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
