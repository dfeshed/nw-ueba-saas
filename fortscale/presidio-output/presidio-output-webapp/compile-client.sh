#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo $M2_HOME
mvn clean install -f $DIR/target/generated-sources/swagger