#!/bin/sh
export JAVA_HOME="..."
java -Xmx16G \
     -jar \
     -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts \
     -Djavax.net.ssl.trustStorePassword=changeit \
     ./target/openeo-spring-driver-1.0.0-draft.jar
