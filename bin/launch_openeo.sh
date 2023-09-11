#!/bin/sh

SRC_DIR={{...}}
GDAL_HOME={{...}}

export JAVA_HOME={{...}}
export GDAL_BIN="${GDAL_HOME}/bin/"
export GDAL_DATA="${GDAL_HOME}/share/gdal/"

export OPENEO_LOGS_DIR=/var/log/openeo

java -Xms4G -Xmx16G \
     -jar \
     -Djavax.net.ssl.trustStore="${JAVA_HOME}/lib/security/cacerts" \
     -Djavax.net.ssl.trustStorePassword=changeit \
     -Djava.library.path="${GDAL_HOME}/share/java/" \
     "${SRC_DIR}/target/openeo-spring-driver-1.1.0-draft.jar"
