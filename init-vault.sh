#!/bin/sh

echo "Waiting for Vault to start..."
sleep 5 &&
SECRET_KEY=$(openssl rand -base64 32)
echo "generated $SECRET_KEY"
curl \
    --header "X-Vault-Token: root" \
    --request POST \
    --data "{ \"data\": {\"secret\":\"$SECRET_KEY\"} }" \
    http://vault:8200/v1/secret/data/jwt-signing