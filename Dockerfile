# SPDX-License-Identifier: Apache-2.0
# Copyright Contributors to the Egeria project

ARG version=latest
ARG EGERIA_BASE_IMAGE=quay.io/odpi/egeria
# DEFER setting this for now, using the ${version}:
# ARG EGERIA_IMAGE_DEFAULT_TAG=latest

# This Dockerfile should be run from the parent directory
# ie
# docker -f ./Dockerfile

FROM ${EGERIA_BASE_IMAGE}:${version}

ENV version ${version}

# Labels from https://github.com/opencontainers/image-spec/blob/master/annotations.md#pre-defined-annotation-keys (with additions prefixed    ext)
# We should inherit all the base labels from the egeria image and only overwrite what is necessary.
LABEL org.opencontainers.image.description = "Egeria with Strimzi connector" \
      org.opencontainers.image.documentation = "https://github.com/odpi/egeria-connector-integration-event-schema"

COPY build/libs/egeria-connector-integration-event-schema-${version}*.jar /deployments/server/lib

# Uncomment to enable Java remote debugging
# ENV JAVA_DEBUG 1
