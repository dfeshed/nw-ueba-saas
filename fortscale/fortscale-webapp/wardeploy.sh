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
sudo /bin/rm -fr "${dst_dir}"
# copy new war
cp -pr ${src_file} ${dst_file}
# restart service
sudo /sbin/service tomcat6 restart
