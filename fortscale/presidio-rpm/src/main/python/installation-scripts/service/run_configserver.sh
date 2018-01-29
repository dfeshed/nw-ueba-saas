#!/bin/bash

echo "verifying configserver HEAD is valid"
cd /home/presidio/presidio-core/configurations/
if git status &> /dev/null ; then
    echo "configserver HEAD is valid"
else
    echo "configserver HEAD is invalid. Fixing..."
    rm -f .git/index
    git reset HEAD .
fi
cd -

echo "Running Presidio config server"
/usr/bin/java -jar /home/presidio/presidio-core/bin/presidio-configuration-server-*.jar
