#!/bin/bash

PORT=80

# Check if the port is in use
PID=`lsof -ti tcp:80`

if [ -n "$PID" ]; then
  echo "Stopping existing app running on port ${PORT} (PID: ${PID})"
  kill $PID && sleep 10
fi

# Start the new instance
echo "Starting new instance..."
java -jar app.jar