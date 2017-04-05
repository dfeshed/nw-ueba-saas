#!/bin/bash
echo Given arguments for task are = $@

COLLECTION_JAR=/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar

if [ ! -f "$COLLECTION_JAR" ]
then
        (>&2 echo  "File $COLLECTION_JAR does not exist")
        exit 1
fi


java -jar ${COLLECTION_JAR} $@
