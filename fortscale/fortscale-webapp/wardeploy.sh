#!/bin/bash
## Deploys fortscale-webapp.war to tomcat webapps folder
echo "Starting WAR Deployment script"
src_file="target/fortscale-webapp-*.war"
# in jenkins jobs WORKSPACE var is defined as root repository folder
if [ ! -z "${WORKSPACE}" ]; then
    echo "Running in Jenkins. WORKSPACE=${WORKSPACE}"
    src_file="${WORKSPACE}/fortscale/fortscale-webapp/target/fortscale-webapp-*.war"
fi
echo "current user: ${USER}, id: `id`" 
echo "current workspace: ${WORKSPACE}"
dst_dir="/var/lib/tomcat6/webapps/fortscale-webapp"
dst_file="${dst_dir}.war"
echo "About to deploy ${src_file} to ${dst_file}"

# cleanup old files
if [ -d ${dst_dir} ]; then
    echo "Cleaning up old deployment app at ${dst_dir}"
    sudo /bin/rm -fr "${dst_dir}" > /dev/null
    res=$?
    if [ $res -ne 0 ]; then
        echo "FATAL: Failed to clean up old webapp folder, exiting"
        exit $res
    fi
fi
# copy new war
echo "Copying the file to its deployment location"
cp ${src_file} ${dst_file}
res=$?
if [ $res -ne 0 ]; then
    echo "FATAL: Failed to deploy the new war file, exiting"
    exit $res
fi

# restart service
echo "Restarting Tomcat service"
sudo /sbin/service tomcat6 restart > /dev/null
res=$?
if [ $res -ne 0 ]; then
    echo "FATAL: Failed to restart tomcat6 service, exiting"
    exit $res
fi
echo "WAR Deployment script completed successfully."
exit 0
