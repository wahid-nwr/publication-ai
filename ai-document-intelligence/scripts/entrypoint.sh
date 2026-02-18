#!/bin/sh
set -e

echo "PORT=$PORT"
echo "TRUSTSTORE_PASSWORD length=${#TRUSTSTORE_PASSWORD}"

exec java \
  -Xms64m \
  -Xmx192m \
  -XX:MaxMetaspaceSize=96m \
  -XX:MaxDirectMemorySize=64m \
  -XX:+UseContainerSupport \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ExitOnOutOfMemoryError \
  -cp "/app/app.jar:/app/lib/*" \
  io.wahid.publication.ai.ServerLauncher
