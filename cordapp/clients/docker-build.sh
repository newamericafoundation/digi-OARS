#!/bin/bash

set -ex

TAG="${1}"
PASSWORD="${2}"
PUSH="${3}"

IMAGE="newamericaacrprod.azurecr.io/oars-client"

if [[ "$(docker images -q "${IMAGE}":"${TAG}" 2> /dev/null)" == "" ]]; then
    docker build -t ${IMAGE}:"${TAG}" -t ${IMAGE}:latest .
fi

if [ "${PUSH}" = true ] ; then
    docker login -u newamericaacrprod -p "${PASSWORD}" newamericaacrprod.azurecr.io
    docker push newamericaacrprod.azurecr.io/oars-client
fi