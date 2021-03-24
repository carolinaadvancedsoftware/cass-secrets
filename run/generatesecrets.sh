#!/bin/sh
# RUN

export JAVA_BIN=/opt/java/jdk-15.0.1/bin
export PROJECT=/mnt/e/carolinaadvancedsoftware/cass-secrets
export LIBS=$PROJECT/run/libs
export JAR=$LIBS/cass-secrets-0.0.1-SNAPSHOT.jar

# export _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true"
# -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005

$JAVA_BIN/java -cp $LIBS -jar $JAR $*
