FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/ai-document-intelligence-1.0-SNAPSHOT.jar app.jar
COPY target/lib ./lib

# OpenShift runs with random UID
RUN chmod 644 app.jar

EXPOSE 8080

CMD ["java", "-Xms256m", "-Xmx1G", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", \
"-cp", "app.jar:lib/*", "io.wahid.publication.ai.ServerLauncher"]
