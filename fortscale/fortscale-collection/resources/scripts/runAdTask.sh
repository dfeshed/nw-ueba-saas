#!/bin/bash
echo Given arguments for task are = $@

java -jar /home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar $1 $2 $3

