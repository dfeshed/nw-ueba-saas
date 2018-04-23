#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CLIENT_VERSION=$1
FILE_NAME="target/presidio-manager-generated-client-"${CLIENT_VERSION}".jar"

mvn deploy:deploy-file -f $DIR/target/generated-sources/swagger -DpomFile=pom.xml -Dfile=$FILE_NAME -DrepositoryId=asoc-snapshots -Durl=https://repo1.rsa.lab.emc.com:8443/artifactory/asoc-snapshots