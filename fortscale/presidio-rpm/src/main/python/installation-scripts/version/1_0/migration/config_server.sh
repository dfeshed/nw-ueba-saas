#!/bin/bash

if [ "$1" = "--uninstall" ]; then

#core rpm preremoveScriptlet already disables configserver service
echo "Stopping config server"
systemctl stop configserver
echo "Sleeping for 5 seconds before deleting configuration service files"
sleep 5
echo "Removing configuration service files"
rm -rf -v /etc/sysconfig/configserver
rm -rf -v /usr/lib/systemd/system/configserver.service
systemctl daemon-reload
rm -rf -v  /var/log/presidio/configurationserver/

else #upgrade

echo "Stopping config server anyway"
systemctl stop configserver # when core rpm preremoveScriptlet disabled the service, it also stopped it but just in case

echo "Creating configuration server log folder"
log_dir=/var/log/presidio/configurationserver/
mkdir $log_dir
chown presidio:presidio $log_dir

echo "Committing configuration"
cd /home/presidio/presidio-core/configurations
git init
git add .
git commit -m "adding configuration files"

systemctl daemon-reload

echo "Adding configserver service to startup and starting it"
systemctl enable --now configserver.service
fi
