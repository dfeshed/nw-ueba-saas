#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
/usr/local/apache-maven/apache-maven-3.3.3/bin/mvn deploy:deploy-file -f $DIR/target/generated-sources/swagger -DpomFile=pom.xml -Dfile=target/presidio-output-generated-client-1.0.0-SNAPSHOT.jar -DrepositoryId=mirror.fortscale.dom -Durl=http://mirror.fortscale.dom/artifactory/presidio-snapshot-local