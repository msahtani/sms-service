#!/bin/sh

# Check if the PID file exists
if [ -f "app.pid" ]; then
  PID=$(cat app.pid)
  echo "Stopping existing app with PID: ${PID}"
  kill -9 $PID
fi

# Start new app instance
echo "Starting new instance..."
nohup java -jar your-app.jar --spring.pid.file=app.pid &