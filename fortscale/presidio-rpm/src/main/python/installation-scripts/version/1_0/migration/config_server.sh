#!/bin/bash

echo "Creating configuration server log folder"
log_dir=/var/log/presidio/configurationserver/
mkdir $log_dir
chown presidio:presidio $log_dir

echo "Committing configuration"
cd /home/presidio/presidio-core/configurations
git init
git add .
git commit -m "adding configuration files"

echo "Copying configserver service file"
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/configserver /etc/init.d/configserver
chmod 755 /etc/init.d/configserver
echo "Stopping config server anyway"
service configserver stop
echo "Adding configserver to chkconfig"
chkconfig --add configserver
echo "Starting config server"
systemctl start configserver


