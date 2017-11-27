#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
/usr/local/apache-maven/apache-maven-3.3.3/bin/mvn clean install -f $DIR/target/generated-sources/swagger