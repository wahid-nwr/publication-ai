FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/ai-document-intelligence-1.0-SNAPSHOT.jar app.jar
COPY target/lib ./lib

# OpenShift runs with random UID
RUN chmod 644 app.jar

EXPOSE 8080

CMD ["java", "-Xms64m", "-Xmx192m", "-XX:MaxMetaspaceSize=96m", "-XX:MaxDirectMemorySize=64m", "-XX:+UseContainerSupport", \
"-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-XX:+ExitOnOutOfMemoryError", "-cp", "app.jar:lib/*", "io.wahid.publication.ai.ServerLauncher"]
