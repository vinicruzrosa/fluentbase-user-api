#!/bin/sh
# Maven Wrapper script
# Downloads and runs Maven if not already cached

set -e

MAVEN_HOME="${HOME}/.m2/wrapper/dists/apache-maven-3.9.9"
MAVEN_CMD="${MAVEN_HOME}/bin/mvn"
WRAPPER_PROPERTIES="$(dirname "$0")/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "${MAVEN_CMD}" ]; then
    echo "Downloading Maven 3.9.9..."
    DIST_URL=$(grep 'distributionUrl' "${WRAPPER_PROPERTIES}" | cut -d'=' -f2-)
    mkdir -p "${MAVEN_HOME}"
    TEMP_FILE=$(mktemp /tmp/maven-XXXXXX.zip)
    curl -sSL -o "${TEMP_FILE}" "${DIST_URL}"
    unzip -qo "${TEMP_FILE}" -d "${HOME}/.m2/wrapper/dists/"
    mv "${HOME}/.m2/wrapper/dists/apache-maven-3.9.9"/* "${MAVEN_HOME}/" 2>/dev/null || true
    rm -f "${TEMP_FILE}"
    chmod +x "${MAVEN_CMD}"
fi

exec "${MAVEN_CMD}" "$@"
