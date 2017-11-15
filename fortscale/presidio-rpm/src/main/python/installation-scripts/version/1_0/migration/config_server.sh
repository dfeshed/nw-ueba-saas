#!/bin/bash

if [ "$1" = "--uninstall" ]; then

echo "Stopping config server"
systemctl stop configserver
echo "Sleeping for 5 seconds before deleting configuration service files"
sleep 5
echo "Removing chkconfig setting"
chkconfig --del configserver
echo "Removing configuration service files"
rm -rf -v /etc/init.d/configserver
systemctl daemon-reload
rm -rf -v  /var/log/presidio/configurationserver/

else

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
systemctl stop configserver
echo "Adding configserver to chkconfig"
chkconfig --add configserver
echo "Starting config server"
systemctl stop configserver
systemctl start configserver
systemctl daemon-reload

fi
