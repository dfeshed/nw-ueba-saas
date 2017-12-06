#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CLIENT_VERSION=$1
FILE_NAME="target/presidio-manager-generated-client-"${CLIENT_VERSION}".jar"

/usr/local/apache-maven/apache-maven-3.3.3/bin/mvn deploy:deploy-file -f $DIR/target/generated-sources/swagger -DpomFile=pom.xml -Dfile=$FILE_NAME -DrepositoryId=mirror.fortscale.dom -Durl=http://mirror.fortscale.dom/artifactory/presidio-snapshot-local