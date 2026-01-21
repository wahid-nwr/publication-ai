# Use a JDK runtime image
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the fat JAR built by Maven into the image
#COPY target/PublicationManagement-1.1-SNAPSHOT.jar app.jar
#COPY target/lib ./lib

# Expose ports if needed
EXPOSE 8081

# Start the application
#ENTRYPOINT ["java", "-jar", "app.jar"]
#ENTRYPOINT ["java", "-cp", "app.jar", "io.wahid.publication.PublicationManagementApplication"]
#ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "io.wahid.publication.PublicationManagementApplication"]