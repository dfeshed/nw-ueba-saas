#!/bin/bash
echo "Copying configserver service file"
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/configserver /etc/init.d/configserver
chmod 755 /etc/init.d/configserver
echo "Stopping config server anyway"
service configserver stop
echo "Adding configserver to chkconfig"
chkconfig --add configserver
echo "Starting config server"
service configserver start
systemctl start configserver


