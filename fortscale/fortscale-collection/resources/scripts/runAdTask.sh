#!/bin/bash
echo args = $1 $2 $3 >> /tmp/runTaskAdInput_$1

java -jar /home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar $1 $2 $3

