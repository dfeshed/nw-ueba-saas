#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mvn clean install -f $DIR/target/generated-sources/swagger