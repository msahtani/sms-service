#!/bin/bash

for i in {1..10};
do
    curl -X POST \
        -H "Content-Type:application/json" \
        -d @json \
        http://34.116.165.208/app-gw/send
done