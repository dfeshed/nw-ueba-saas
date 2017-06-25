#!/bin/bash
echo "Copying configserver service file"
sudo cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/configserver /etc/init.d/configserver.service
echo "Stopping config server anyway"
sudo systemctl stop configserver
echo "Adding configserver to chkconfig"
sudo chkconfig --add configserver
echo "Starting config server"
sudo service configserver start


