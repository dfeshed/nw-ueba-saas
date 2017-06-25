#!/bin/bash
echo "content of ../utils/:"
ls -l /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/configserver /etc/init.d/
echo "we are here"
pwd
systemctl stop configserver
sudo chkconfig --add configserver
systemctl start configserver


