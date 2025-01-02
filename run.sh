#!/bin/bash

PORT=80

# Check if the port is in use
PID=`lsof -ti tcp:80`

if [[ ! -z "$PID" ]]; then
  echo "Stopping existing app running on port ${PORT} (PID: ${PID})"
  kill $PID && sleep 10
fi

# Start the new instance
echo "Starting new instance..."

set +e
echo "java -jar app.jar > output.log" | at now + 1 min