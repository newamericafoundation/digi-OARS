#!/bin/bash

set -ex

TAG="${1}"

docker build -t newamericapocacr.azurecr.io/oars-ui:${TAG} -t newamericapocacr.azurecr.io/oars-ui:latest .
docker push newamericapocacr.azurecr.io/oars-ui