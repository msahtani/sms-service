# SMS Service

A spring boot based app that handle SSE connection with the phone agent 
in order to send sms on-demand

## APIs
* `GET /app-gw`: establish SSE connection with the server
* `POST app-gw/send`: send SMS to the mobile agent