#!/bin/bash

set -ex

TAG="${1}"
PASSWORD="${2}"
PUSH="${3}"

IMAGE="newamericapocacr.azurecr.io/oars-ui"

if [[ "$(docker images -q "${IMAGE}":"${TAG}" 2> /dev/null)" == "" ]]; then
    docker build -t ${IMAGE}:${TAG} -t ${IMAGE}:latest .
fi

if [ "${PUSH}" = true ] ; then
    docker login -u newamericapocacr -p "${PASSWORD}" newamericapocacr.azurecr.io
    docker push newamericapocacr.azurecr.io/oars-ui
fi