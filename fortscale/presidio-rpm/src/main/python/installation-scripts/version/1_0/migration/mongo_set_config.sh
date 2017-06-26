#!/bin/bash

echo "Mongo: stopping service"
systemctl stop mongod
systemctl status mongod
echo "Mongo: backuping configuration "
mv /etc/mongod.conf /etc/mongod.conf.bkp
echo "Mongo: Stopping authentication flag"
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf.no_auth /etc/mongod.conf
echo "Mongo: restarting service"
systemctl start mongod
wait ${!}
systemctl status mongod
sleep 30
echo "Mongo: creating superadmin user"
mongo localhost:27017/admin /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongo_create_root.js
sleep 30
echo "Mongo: creating presidio user"
mongo localhost:27017/presidio /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongo_create_presidio_admin.js
echo "Mongo: stopping service"
systemctl stop mongod
systemctl status mongod
echo "Mongo: importing & backuping configuration files "
mv /etc/mongod.conf /etc/mongod.conf.no_auth
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf /etc/mongod.conf.auth
cp /home/presidio/presidio-core/installation/installation-scripts/infrastructure/deploy/manager/../../../version/1_0/utils/mongod.conf /etc/mongod.conf
echo "Mongo: starting service"
systemctl start mongod
systemctl status mongod