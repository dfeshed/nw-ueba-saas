#!/bin/bash
#---
## build presidio-netwitness-flume
#---
echo "Current location: $PWD"
echo "running mvn clean install on presidio-netwitness-flume"
mvn clean install ../../../presidio-core-extension/presidio-netwitness-flume/pom.xml
echo "finished running mvn clean install on presidio-netwitness-flume"