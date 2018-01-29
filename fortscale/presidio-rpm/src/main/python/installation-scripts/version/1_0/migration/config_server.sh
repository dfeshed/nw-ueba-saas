#!/bin/bash

if [ "$1" = "--uninstall" ]; then

echo "Removing configserver service to startup and stopping it"
systemctl disable --now configserver.service
echo "Sleeping for 5 seconds before deleting configuration service files"
sleep 5
echo "Removing configuration service files"
rm -rf -v /etc/sysconfig/configserver
rm -rf -v /usr/lib/systemd/system/configserver.service
systemctl daemon-reload
rm -rf -v  /var/log/presidio/configurationserver/

else #install

echo "Stopping config server anyway"
systemctl stop configserver

echo "Creating configuration server log folder"
log_dir=/var/log/presidio/configserver/
mkdir $log_dir
chown presidio:presidio $log_dir

echo "Committing configuration"
cd /home/presidio/presidio-core/configurations
git init
git add .
git commit -m "adding configuration files"

echo "Copying configserver service files to their designated folders"
cp /home/presidio/presidio-core/installation/installation-scripts/service/configserver.service /usr/lib/systemd/system/
cp /home/presidio/presidio-core/installation/installation-scripts/service/configserver /etc/sysconfig/

systemctl daemon-reload

echo "Adding configserver service to startup and starting it"
systemctl enable --now configserver.service

#making sure configserver HEAD is valid
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
fi
