#!/bin/bash
## Deploys fortscale-webapp.war to tomcat webapps folder
src_file="target/fortscale-webapp-*-SNAPSHOT.war"
# in jenkins jobs WORKSPACE var is defined as root repository folder
if [ ! -z "${WORKSPACE}" ]; then
    echo "Running in Jenkins. WORKSPACE=${WORKSPACE}"
    src_file="${WORKSPACE}/fortscale/fortscale-webapp/target/fortscale-webapp-*-SNAPSHOT.war"
fi
dst_dir="/var/lib/tomcat6/webapps/fortscale-webapp"
dst_file="${dst_dir}.war"

# cleanup old files
if [ -d ${dst_dir} ]; then
    sudo /bin/rm -fr "${dst_dir}"
    res=$?
    if [ $res -ne 0 ]; then
        echo "FATAL: Failed to clean up old webapp folder, exiting"
        exit $res
    fi
fi
# copy new war
cp -pr ${src_file} ${dst_file}
res=$?
if [ $res -ne 0 ]; then
    echo "FATAL: Failed to deploy the new war file, exiting"
    exit $res
fi

# restart service
sudo /sbin/service tomcat6 restart
res=$?
if [ $res -ne 0 ]; then
    echo "FATAL: Failed to restart tomcat6 service, exiting"
    exit $res
fi
exit 0
